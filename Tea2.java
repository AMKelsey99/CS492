package pkg493final;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
/**
 *
 * @author alana
 */
public class Tea2 {
    //Plaintext: 0x0FCA4567 0x0CABCDEF
    //key: 0xBF6BABCD EF00F000 FEAFAFAF ACCDEF01
    int L, R, delta; //using int because parsing the keys to int is too big for an integer
    int[] k = new int[4]; //key
    int[] text = new int[2]; //split into 2 32bits
            
    public Tea2() {
        delta = 0x9e3779b9;
    }
    
    /**
     * Encrypts using TEA algorithm
     * @return returns encrypted hex block
     */
    public int[] tea2Encrypt() {
        L = text[0];
        R = text[1];
        int sum = 0;
        for (int i = 0; i<32; i++) {
            sum += delta;
            L += ((R<<4 & 0xfffffff0)+k[0])^(R+sum)^((R>>5 & 0x7ffffff)+k[1]);
            R += ((L<<4 & 0xfffffff0)+k[2])^(L+sum)^((L>>5 & 0x7ffffff)+k[3]);
        }
        //System.out.println(String.format("ENCRYPTED: %x", L) + " " + String.format("%x", R));  
        text[0] = L;
        text[1] = R;
        return text;
    }
    
    /**
     * Decrypts using TEA algorithm
     * @return returns decrypted hex block
     */
    public int[] tea2Decrypt() {
        L = text[0];
        R = text[1];
        int sum = delta  << 5;
        for (int i = 0; i<32; i++) {
            R -= ((L<<4 & 0xfffffff0)+k[2])^(L+sum)^((L>>5 & 0x7ffffff)+k[3]);
            L -= ((R<<4 & 0xfffffff0)+k[0])^(R+sum)^((R>>5 & 0x7ffffff)+k[1]);
            sum -= delta;
        }
        text[0] = L;
        text[1] = R;
        return text;
    }
    
    /**
     * Combine halves to final 64bit string
     * @return 
     */
    public String decryptOut() {
        String lef = Integer.toHexString(text[0]);
        String rig = Integer.toHexString(text[1]);
        String outString = lef +"" + rig;
        return outString;
    }
    
    /**
     * Prints out the string to the console
     */
    public void printText() {
        String out = "";
        for(int i:text) {
            out += Integer.toHexString(i);
        }
        System.out.print("0x" + out + "\n");
        
    }
    
    /**
     * Prints out the encrypted hex string to file
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void toFile() throws FileNotFoundException, IOException {
        File file = new File("encrypted.txt");
        PrintWriter pw = new PrintWriter(new FileWriter(file, true));
        String out = "";
        for (int i:text) {
            out += Integer.toHexString(i);
        }
        pw.println("0x" + out);
        pw.close();
    }
    
    /**
     * Split the 64bit block into two 32 bit halves
     * @param s Hex string to split
     * @return 
     */
    public int[] splitPlaintext(String s) {
        s = "0x" + s;
        String left = s.substring(2, 10);
        String right = s.substring(10, 18);
        int lef = Integer.parseUnsignedInt(left, 16);
        int rig = Integer.parseUnsignedInt(right, 16);
        this.text[0] = lef;
        this.text[1] = rig;
        return text;
    }
    
    /**
     * Identical to splitPlaintext, but appends 0x to the front of the string before doing so
     * @param s Hex string to split
     * @return 
     */
        public int[] dsplitPlaintext(String s) {
        String left = s.substring(2, 10);
        String right = s.substring(10, 18);
        int lef = Integer.parseUnsignedInt(left, 16);
        int rig = Integer.parseUnsignedInt(right, 16);
        this.text[0] = lef;
        this.text[1] = rig;
        return text;
    }
    
        /**
         * Splits the 128bit key into 4 blocks
         * @param s Hex string to split
         * @return Integer array of the 128bit key
         */
    public int[] splitKey(String s) {
        String k1, k2, k3, k4;
        k1 = s.substring(2, 10);
        k2 = s.substring(10, 18);
        k3 = s.substring(18, 26);
        k4 = s.substring(26);
        
        int key1 = Integer.parseUnsignedInt(k1, 16);
        int key2 = Integer.parseUnsignedInt(k2, 16);
        int key3 = Integer.parseUnsignedInt(k3, 16);
        int key4 = Integer.parseUnsignedInt(k4, 16);
        //System.out.println(String.format("KEYS: %x", key1) + " " + String.format("%x", key2)+ " " + String.format("%x", key3)+ " " + String.format("%x", key4));  
        this.k[0] = key1;
        this.k[1] = key2;
        this.k[2] = key3;
        this.k[3] = key4;
        return k;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        Scanner k = new Scanner(System.in);
        File file = new File("secretFile.txt");
        FileReader f = new FileReader(file);
        String in = f.toString();
        System.out.println(in);
        
        Tea2 tea2 = new Tea2();
        String p, key;
        
        System.out.println("Enter 64-bit Plaintext: ");
        p = k.nextLine();
        String sub = p.substring(2);
        while(sub.length() < 16) {
            sub = "0" + sub;
        }
        p = "0x" + sub;
        
        System.out.println("Enter 128-bit key: ");
        key = k.nextLine();
        sub = key.substring(2);
        while(sub.length() < 32) {
            sub = "0" + sub;
        }
        key = "0x" + sub;
        
        //tea2.splitPlaintext("0x0123456789abcdef");
        //tea2.splitKey("0xa56babcdf000ffffffffffffabcdef01");
        
        tea2.splitPlaintext(p);
        tea2.splitKey(key);
        System.out.print("Original: ");
        tea2.printText();
        
        tea2.tea2Encrypt();
        System.out.print("Cipher: ");
        tea2.printText();
        
        tea2.tea2Decrypt();
        System.out.print("Decrypted: ");
        tea2.printText();
    }
    
}
