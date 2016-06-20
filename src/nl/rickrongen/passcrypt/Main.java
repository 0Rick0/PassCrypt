package nl.rickrongen.passcrypt;

import nl.rickrongen.passcrypt.util.AesPassManager;

import java.util.Scanner;

public class Main {

    private static String helpText =
            "Commands:\n" +
                    "   HELP\n" +
                    "   ENCRYPT" +
                    "   DECRYPT" +
                    "   EXIT";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running){
            String input = scanner.nextLine();
            switch (input.trim().toUpperCase()){
                case "ENCRYPT":
                    System.out.println("Input");
                    String inputdata = scanner.nextLine();
                    System.out.println("Output File");
                    String outputfile = scanner.nextLine();
                    System.out.println("Password");
                    char[] password = scanner.nextLine().toCharArray();
                    System.gc();//collect the password string
                    System.out.println(AesPassManager.getInstance().encrypt(inputdata,outputfile,password)? "success" : "fail");
                    break;
                case "DECRYPT":
                    System.out.println("Input File");
                    String inputfile = scanner.nextLine();
                    System.out.println("Password");
                    char[] dpassword = scanner.nextLine().toCharArray();
                    System.gc();//collect the password string
                    System.out.println(AesPassManager.getInstance().decrypt(inputfile,dpassword));
                    break;
                case "HELP":
                    System.out.println(helpText);
                    break;
                case "EXIT":
                    running=false;
                    break;
                default:
                    System.out.println("unknown command, type help for help");
                    break;
            }
        }
    }
}
