public class VGN {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String encrypt(String plainText, String key) {
        StringBuilder encryptedText = new StringBuilder();
        plainText = plainText.toUpperCase();
        key = key.toUpperCase();
        int keyIndex = 0;

        for (int i = 0; i < plainText.length(); i++) {
            char c = plainText.charAt(i);

            if (Character.isLetter(c)) {
                int plainCharIndex = ALPHABET.indexOf(c);
                int keyCharIndex = ALPHABET.indexOf(key.charAt(keyIndex % key.length()));

                int encryptedCharIndex = (plainCharIndex + keyCharIndex) % ALPHABET.length();
                char encryptedChar = ALPHABET.charAt(encryptedCharIndex);

                encryptedText.append(encryptedChar);

                keyIndex++;
            } else {
                encryptedText.append(c);
            }
        }

        return encryptedText.toString();
    }

    public static String decrypt(String encryptedText, String key) {
        StringBuilder decryptedText = new StringBuilder();
        encryptedText = encryptedText.toUpperCase();
        key = key.toUpperCase();
        int keyIndex = 0;

        for (int i = 0; i < encryptedText.length(); i++) {
            char c = encryptedText.charAt(i);

            if (Character.isLetter(c)) {
                int encryptedCharIndex = ALPHABET.indexOf(c);
                int keyCharIndex = ALPHABET.indexOf(key.charAt(keyIndex % key.length()));

                int plainCharIndex = (encryptedCharIndex - keyCharIndex + ALPHABET.length()) % ALPHABET.length();
                char plainChar = ALPHABET.charAt(plainCharIndex);

                decryptedText.append(plainChar);

                keyIndex++;
            } else {
                decryptedText.append(c);
            }
        }

        return decryptedText.toString();
    }

    public static void main(String[] args) {
        String plainText = "HELLO";
        String key = "KEY";

        String encryptedText = encrypt(plainText, key);
        System.out.println("Encrypted text: " + encryptedText);

        String decryptedText = decrypt(encryptedText, key);
        System.out.println("Decrypted text: " + decryptedText);
    }
}
