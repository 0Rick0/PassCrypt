package nl.rickrongen.passcrypt.util;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;

/**
 * Created by Rick on 20-6-2016.
 */
public class AesPassManager
{
    private static AesPassManager instance;
    public static AesPassManager getInstance() {
        return instance == null ? (instance = new AesPassManager()) : instance;
    }

    public boolean encrypt(String input, String outputFile, char[] password) {
        try {
            SecureRandom rand = SecureRandom.getInstance("SHA1PRNG");

            byte[] salt = new byte[8];
            rand.nextBytes(salt);
            SecretKey secret = getSecret(password,salt);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE,secret);

            AlgorithmParameters params = cipher.getParameters();
            byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            byte[] ciphertext = cipher.doFinal(input.getBytes("UTF-8"));
            DataOutputStream dout = new DataOutputStream(new FileOutputStream(outputFile));
            dout.writeInt(salt.length);
            dout.writeInt(iv.length);
            dout.writeInt(ciphertext.length);
            dout.write(salt);
            dout.write(iv);
            dout.write(ciphertext);
            dout.close();
            return true;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException | NoSuchPaddingException | InvalidParameterSpecException | IOException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String decrypt(String file, char[] password){
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            int saltlength = dis.readInt();
            int ivlength = dis.readInt();
            int datalength = dis.readInt();
            byte[] salt = new byte[saltlength];
            byte[] iv = new byte[ivlength];
            byte[] data = new byte[datalength];
            dis.read(salt);
            dis.read(iv);
            dis.read(data);

            SecretKey secret = getSecret(password, salt);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
            String plain = new String(cipher.doFinal(data), "UTF-8");

            return plain;
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private SecretKey getSecret(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(password,salt,65536,128);
        SecretKey tmp = factory.generateSecret(spec);
        for (int i = 0; i < password.length; i++) {
            password[1] = 0x00;//delete password
        }
        return new SecretKeySpec(tmp.getEncoded(),"AES");
    }
}
