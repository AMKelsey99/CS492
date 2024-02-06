/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg493final;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * NOTE: The encryption will always read from secretFile.txt
 * @author alana
 */
public class encryptDecrypt {
    
    /**
     * This converts plaintext from the file to an array of hex strings
     * @param content Plaintext string read in from file path
     * @return Returns an array of hex strings
     */
    public static String[] plainToHex(String content) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] toHex = content.toCharArray();
        for (char c : toHex) {
            String hexString = Integer.toHexString(c);
            stringBuilder.append(hexString);
        }
        String out = stringBuilder.toString();
        
        int size = out.length();
        while (size%16 != 0) {
            out = "0" + out;
            size = out.length();
        }
        size = out.length();
        int index = 0;
        while (size != 0) {
            size -= 16;
            index++;
        }
        
        String[] inputs = new String[index];
        for (int i = 0; i <= index-1; i++) {
            inputs[i] = out.substring(0+(16*i), 16+(16*i));
        }
        return inputs;
    }
    
    /**
     * 
     * @param i Encrypted Hex String from file
     * @return Reads in from file the contents and returns it to a string array until all lines have been read
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static String[] hexToDecrypt(String i) throws FileNotFoundException, IOException {
        File file = new File(i);
        BufferedReader read = new BufferedReader(new FileReader(file));
        Path path = Paths.get(i);
        long lines = Files.lines(path).count();
        int index = Math.toIntExact(lines);
        String[] nextLine = new String[index];
        
        for (int size = 0; size < index; size++) {
            nextLine[size] = read.readLine();
        }
        return nextLine;
    }
    
    /**
     * 
     * @param in Array of encrypted hex strings
     * @param t Instance of TEA algorithm
     * @param key Key for TEA
     * @return returns the decrypted plain Hex strings as one string 
     */
    public static String decryptToPlainHex(String[] in, Tea2 t, String key) {
        String out = "";
        StringBuilder sb = new StringBuilder();
        
        String outArray[] = new String[in.length];
        
        for (int i = 0; i < in.length; i++) {
            t.dsplitPlaintext(in[i]);
            t.splitKey(key);
            t.tea2Decrypt();
            outArray[i] = t.decryptOut();
        }
        
        for (String s:outArray) {
            out += s;
        }
        return out;
    }
    
    /**
     * Read in plaintext file to encrypt, and converts it to hex, then calls TEA to encrypt each 64bit block
     * @param secret The secret key from Shamir's Secret Sharing
     * @param tea2 Instance of TEA algorithm
     * @param filename File path to read in from
     * @throws IOException 
     */
    public static void encryptHex(String secret, Tea2 tea2, String filename) throws IOException {
        //Path path = Paths.get("secretFile.txt");
        Path path = Paths.get(filename);
        String content = Files.readString(path, StandardCharsets.US_ASCII);
        String[] plainHex = plainToHex(content);
        PrintWriter writer = new PrintWriter("encrypted.txt"); //clear file
        writer.print("");
        writer.close();
        for (String plain : plainHex) {
        
        tea2.splitPlaintext(plain);
        tea2.splitKey(secret);
        tea2.tea2Encrypt();
        tea2.toFile();
        }
    }
    
    /**
     * Continuously parse each char and match it to its ASCII char value, append to output string
     * @param in Hex string input
     * @return Plaintext string message (letters)
     */
    public static String hexToPlain(String in) {
        in = in.toUpperCase();
        StringBuilder output = new StringBuilder("");
        String str;
    
    for (int i = 0; i < in.length()-1; i += 2) {
        switch (i) {
            case 0:
                if (in.substring(i,i+1).equals("0")) {
                    i++;
                } else {
                str = in.substring(i, i + 2);
                output.append((char) Integer.parseInt(str, 16));
                break;
                }
            default:
                str = in.substring(i, i + 2);
                output.append((char) Integer.parseInt(str, 16));
                break;
        }
        
    }
    return output.toString();
    }
    
    /**
     * Prints a string to a specified file path
     * @param in String to print
     * @param filename Path to print to
     * @throws FileNotFoundException 
     */
    public static void outToFile(String in, String filename) throws FileNotFoundException {
        File file = new File(filename);
        PrintWriter pw = new PrintWriter(file);
        
        pw.print(in);
        pw.close();
    }
}
