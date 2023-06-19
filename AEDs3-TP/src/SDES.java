import java.util.BitSet;
import java.util.Arrays;

public class SDES {
    // Tabelas de permutação e substituição utilizadas no algoritmo

    // Tabela de permutação P10
    private static final int[] P10 = { 3, 5, 2, 7, 4, 10, 1, 9, 8, 6 };
    
    // Tabela de permutação P8
    private static final int[] P8 = { 6, 3, 7, 4, 8, 5, 10, 9 };
    
    // Tabela de permutação IP (Permutação Inicial)
    private static final int[] IP = { 2, 6, 3, 1, 4, 8, 5, 7 };
    
    // Tabela de permutação EP (Permutação de Expansão)
    private static final int[] EP = { 4, 1, 2, 3, 2, 3, 4, 1 };
    
    // Tabela de permutação P4
    private static final int[] P4 = { 2, 4, 3, 1 };
    
    // Tabela de permutação IP^-1 (Permutação Inversa da Permutação Inicial)
    private static final int[] IP_INVERSE = { 4, 1, 3, 5, 7, 2, 8, 6 };
    
    // Matriz de substituição S0
    private static final int[][] S0 = {
            { 1, 0, 3, 2 },
            { 3, 2, 1, 0 },
            { 0, 2, 1, 3 },
            { 3, 1, 3, 2 }
    };
    
    // Matriz de substituição S1
    private static final int[][] S1 = {
            { 0, 1, 2, 3 },
            { 2, 0, 1, 3 },
            { 3, 0, 1, 0 },
            { 2, 1, 0, 3 }
    };

    private BitSet key;
    private BitSet subKey1;
    private BitSet subKey2;

    public SDES() {
        // Construtor padrão, configura a chave padrão para o algoritmo
        subKey1 = new BitSet(8);
        subKey2 = new BitSet(8);

        key = new BitSet(10);
        key.set(1);
        key.set(3);
        key.set(5);
        key.set(7);
        key.set(9);
        setKey(key);
    }

    public String encrypt(String plaintext) {
        // Método para criptografar uma sequência de texto
        BitSet bitSet = stringToBitSet(plaintext);
        BitSet ciphertext = encrypt(bitSet);
        return bitSetToString(ciphertext);
    }

    public String decrypt(String ciphertext) {
        // Método para descriptografar uma sequência de texto criptografado
        BitSet bitSet = stringToBitSet(ciphertext);
        BitSet plaintext = decrypt(bitSet);
        return bitSetToString(plaintext);
    }

    public BitSet encrypt(BitSet plaintext) {
        // Método para criptografar um BitSet
        generateSubKeys();

        BitSet permutedPlaintext = permute(plaintext, IP);
        BitSet fkOutput1 = fk(permutedPlaintext, subKey1);

        BitSet swapped = swapBits(fkOutput1);
        BitSet fkOutput2 = fk(swapped, subKey2);

        BitSet encrypted = permute(fkOutput2, IP_INVERSE);

        return encrypted;
    }

    public BitSet decrypt(BitSet ciphertext) {
        // Método para descriptografar um BitSet criptografado
        generateSubKeys();

        BitSet permutedCiphertext = permute(ciphertext, IP);
        BitSet fkOutput1 = fk(permutedCiphertext, subKey2);

        BitSet swapped = swapBits(fkOutput1);
        BitSet fkOutput2 = fk(swapped, subKey1);

        BitSet decrypted = permute(fkOutput2, IP_INVERSE);

        return decrypted;
    }

    public void setKey(BitSet key) {
        // Método para definir a chave utilizada pelo algoritmo
        this.key = key;
        generateSubKeys();
    }

    private void generateSubKeys() {
        // Método para gerar as subchaves a partir da chave principal
        BitSet permutedKey = permute(key, P10);
        BitSet leftKey = leftShift(permutedKey, 1);
        BitSet rightKey = leftShift(permutedKey, 2);
        subKey1 = permute(leftKey, P8);
        subKey2 = permute(rightKey, P8);
    }

    private BitSet permute(BitSet input, int[] permutationTable) {
        // Método para realizar uma permutação em um BitSet de acordo com uma tabela de permutação
        BitSet output = new BitSet(permutationTable.length);
        for (int i = 0; i < permutationTable.length; i++) {
            output.set(i, input.get(permutationTable[i] - 1));
        }
        return output;
    }

    private BitSet leftShift(BitSet input, int count) {
        // Método para realizar um deslocamento à esquerda em um BitSet
        BitSet output = (BitSet) input.clone();
        output.clear(0, count);
        for (int i = input.length() - count; i < input.length(); i++) {
            output.set(i, input.get(i - input.length() + count));
        }
        return output;
    }

    private BitSet fk(BitSet input, BitSet subKey) {
        // Método para realizar a função de Feistel (FK)
        BitSet expanded = permute(input, EP);
        expanded.xor(subKey);

        BitSet left = new BitSet(4);
        left.set(0, expanded.get(0));
        left.set(1, expanded.get(3));
        left.set(2, expanded.get(2));
        left.set(3, expanded.get(1));

        BitSet right = new BitSet(4);
        right.set(0, expanded.get(4));
        right.set(1, expanded.get(7));
        right.set(2, expanded.get(6));
        right.set(3, expanded.get(5));

        BitSet sBoxOutput = sBox(left, right);

        BitSet p4Output = permute(sBoxOutput, P4);

        BitSet fkOutput = (BitSet) p4Output.clone();
        fkOutput.xor(input);

        return fkOutput;
    }

    private BitSet sBox(BitSet left, BitSet right) {
        // Método para realizar as substituições S-Box
        int row1 = getDecimalValue(left.get(0), left.get(3));
        int col1 = getDecimalValue(left.get(1), left.get(2));
        int sBox1Value = S0[row1][col1];

        int row2 = getDecimalValue(right.get(0), right.get(3));
        int col2 = getDecimalValue(right.get(1), right.get(2));
        int sBox2Value = S1[row2][col2];

        BitSet sBoxOutput = new BitSet(4);
        sBoxOutput.set(0, (sBox1Value & 2) != 0);
        sBoxOutput.set(1, (sBox1Value & 1) != 0);
        sBoxOutput.set(2, (sBox2Value & 2) != 0);
        sBoxOutput.set(3, (sBox2Value & 1) != 0);

        return sBoxOutput;
    }

    private int getDecimalValue(boolean bit1, boolean bit2) {
        // Método para obter o valor decimal correspondente a dois bits
        if (bit1 && bit2) {
            return 3;
        } else if (bit1) {
            return 2;
        } else if (bit2) {
            return 1;
        } else {
            return 0;
        }
    }

    private BitSet swapBits(BitSet input) {
        // Método para trocar os bits do lado esquerdo pelos bits do lado direito
        BitSet output = new BitSet(8);
        output.set(0, input.get(4));
        output.set(1, input.get(5));
        output.set(2, input.get(6));
        output.set(3, input.get(7));
        output.set(4, input.get(0));
        output.set(5, input.get(1));
        output.set(6, input.get(2));
        output.set(7, input.get(3));
        return output;
    }

    private BitSet stringToBitSet(String input) {
        // Método para converter uma sequência de texto em um BitSet
        byte[] bytes = input.getBytes();
        BitSet bitSet = new BitSet(bytes.length * 8);
        int bitIndex = 0;
        for (int i = 0; i < bytes.length; i++) {
            for (int j = 7; j >= 0; j--) {
                bitSet.set(bitIndex++, (bytes[i] >> j & 1) == 1);
            }
        }
        return bitSet;
    }

    private String bitSetToString(BitSet input) {
        // Método para converter um BitSet em uma sequência de texto
        byte[] bytes = new byte[(input.length() + 7) / 8];
        for (int i = 0; i < input.length(); i++) {
            if (input.get(i)) {
                bytes[i / 8] |= 1 << (7 - (i % 8));
            }
        }
        return new String(bytes);
    }
}
