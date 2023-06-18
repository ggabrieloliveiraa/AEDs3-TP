import java.math.BigInteger;
import java.util.*;
import java.io.*;

public class RSA {
    private static final Random random = new Random();
    private BigInteger privateKey;
    private BigInteger publicKey;
    private BigInteger modulus;

    public void encryptRSA(Movie j_temp, boolean option) {
        //System.out.println("Objeto original: " + j_temp);

        // Geração de chaves pública e privada
        generateKeys();

        if (option == true){
        // Criptografar os campos de texto do objeto
        Movie encryptedMovie = encryptFields(j_temp);
        } else {
        // Descriptografar os campos do objeto criptografado
        Movie decryptedMovie = decryptFields(j_temp);

        }
        
        
        //System.out.println("Objeto original: " + j_temp);
        //System.out.println("Objeto criptografado: " + encryptedMovie);
        //System.out.println("Objeto descriptografado: " + decryptedMovie);
    }

    private boolean checkKeyFileExists() {
        File file = new File("keysRSA.txt");
        return file.exists();
    }

    private void saveKeysToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("keysRSA.txt"));
            writer.write(modulus.toString());
            writer.newLine();
            writer.write(publicKey.toString());
            writer.newLine();
            writer.write(privateKey.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadKeysFromFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("keysRSA.txt"));
            modulus = new BigInteger(reader.readLine());
            publicKey = new BigInteger(reader.readLine());
            privateKey = new BigInteger(reader.readLine());
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void generateKeys() {
        if (checkKeyFileExists()) {
            loadKeysFromFile();
        } else {
            BigInteger p = BigInteger.probablePrime(512, random);
            BigInteger q = BigInteger.probablePrime(512, random);

            modulus = p.multiply(q);
            // (p - 1) * (q - 1)
            BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

            publicKey = BigInteger.valueOf(65537);

            privateKey = publicKey.modInverse(phi);

            saveKeysToFile();
        }
    }


    public Movie encryptFields(Movie j_temp) {
        String encTitle = encryptText(j_temp.title);
        
        String[] encGenre = new String[j_temp.genre.length];
        for (int i = 0; i < j_temp.genre.length; i++){
            encGenre[i] = encryptText(j_temp.genre[i]);
        }
        j_temp.title = encTitle;
        
        j_temp.genre = encGenre;

        //System.out.println(j_temp);
        //int encryptedAge = encryptInteger(person.getAge());
        return (j_temp);
    }

    public String encryptText(String text) {
        BigInteger textValue = new BigInteger(text.getBytes());
        BigInteger encryptedValue = textValue.modPow(publicKey, modulus);
        return encryptedValue.toString();
    }

    public Movie decryptFields(Movie j_temp) {
        String decTitle = decryptText(j_temp.title);
        
        String[] decGenre = new String[j_temp.genre.length];
        for (int i = 0; i < j_temp.genre.length; i++){
            decGenre[i] = decryptText(j_temp.genre[i]);
        }
        j_temp.title = decTitle;
        
        
        j_temp.genre = decGenre;

        return j_temp;
    }
  

    public String decryptText(String encryptedValue) {
        BigInteger encryptedBigInt = new BigInteger(encryptedValue);
        BigInteger decryptedBigInt = encryptedBigInt.modPow(privateKey, modulus);
        byte[] decryptedBytes = decryptedBigInt.toByteArray();
        String decryptedString = new String(decryptedBytes);
        return decryptedString;
    }

    
/* 
    public int encryptInteger(int value) {
        BigInteger integerValue = BigInteger.valueOf(value);
        BigInteger encryptedValue = integerValue.modPow(publicKey, modulus);
        return encryptedValue.intValue();
    }
     */
}