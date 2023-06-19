public class VGN {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String encrypt(String plainText, String key) {
        StringBuilder encryptedText = new StringBuilder();
        plainText = plainText.toUpperCase(); // Converte o texto para maiúsculas
        key = key.toUpperCase(); // Converte a chave para maiúsculas
        int keyIndex = 0;

        for (int i = 0; i < plainText.length(); i++) {
            char c = plainText.charAt(i);

            if (Character.isLetter(c)) { // Verifica se o caractere é uma letra
                int plainCharIndex = ALPHABET.indexOf(c); // Obtém o índice da letra no alfabeto
                int keyCharIndex = ALPHABET.indexOf(key.charAt(keyIndex % key.length())); // Obtém o índice da letra da chave correspondente

                int encryptedCharIndex = (plainCharIndex + keyCharIndex) % ALPHABET.length(); // Calcula o novo índice da letra criptografada
                char encryptedChar = ALPHABET.charAt(encryptedCharIndex); // Obtém a letra criptografada correspondente ao novo índice

                encryptedText.append(encryptedChar); // Adiciona a letra criptografada ao texto criptografado

                keyIndex++; // Move para a próxima letra da chave
            } else {
                encryptedText.append(c); // Se não for uma letra, mantém o caractere original no texto criptografado
            }
        }

        return encryptedText.toString(); // Retorna o texto criptografado
    }

    public static String decrypt(String encryptedText, String key) {
        StringBuilder decryptedText = new StringBuilder();
        encryptedText = encryptedText.toUpperCase(); // Converte o texto criptografado para maiúsculas
        key = key.toUpperCase(); // Converte a chave para maiúsculas
        int keyIndex = 0;

        for (int i = 0; i < encryptedText.length(); i++) {
            char c = encryptedText.charAt(i);

            if (Character.isLetter(c)) { // Verifica se o caractere é uma letra
                int encryptedCharIndex = ALPHABET.indexOf(c); // Obtém o índice da letra criptografada no alfabeto
                int keyCharIndex = ALPHABET.indexOf(key.charAt(keyIndex % key.length())); // Obtém o índice da letra da chave correspondente

                int plainCharIndex = (encryptedCharIndex - keyCharIndex + ALPHABET.length()) % ALPHABET.length(); // Calcula o novo índice da letra descriptografada
                char plainChar = ALPHABET.charAt(plainCharIndex); // Obtém a letra descriptografada correspondente ao novo índice

                decryptedText.append(plainChar); // Adiciona a letra descriptografada ao texto descriptografado

                keyIndex++; // Move para a próxima letra da chave
            } else {
                decryptedText.append(c); // Se não for uma letra, mantém o caractere original no texto descriptografado
            }
        }

        return decryptedText.toString(); // Retorna o texto descriptografado
    }
}
