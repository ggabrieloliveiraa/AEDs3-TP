import java.util.BitSet;
    import java.util.Arrays;

public class SDES {
    private static final int[] P10 = { 3, 5, 2, 7, 4, 10, 1, 9, 8, 6 };
    private static final int[] P8 = { 6, 3, 7, 4, 8, 5, 10, 9 };
    private static final int[] IP = { 2, 6, 3, 1, 4, 8, 5, 7 };
    private static final int[] EP = { 4, 1, 2, 3, 2, 3, 4, 1 };
    private static final int[] P4 = { 2, 4, 3, 1 };
    private static final int[] IP_INVERSE = { 4, 1, 3, 5, 7, 2, 8, 6 };
    private static final int[][] S0 = {
            { 1, 0, 3, 2 },
            { 3, 2, 1, 0 },
            { 0, 2, 1, 3 },
            { 3, 1, 3, 2 }
    };
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
    /*
    public String encrypt (String tmp){
        return bitSetToString(encrypt(stringToBitSet(tmp)));
    }
    public String decrypt (String tmp){
        return bitSetToString(decrypt(stringToBitSet(tmp)));
    }
    */

    public String encrypt(String plaintext) {
        BitSet bitSet = stringToBitSet(plaintext);
        BitSet ciphertext = encrypt(bitSet);
        return bitSetToString(ciphertext);
    }

    public String decrypt(String ciphertext) {
        BitSet bitSet = stringToBitSet(ciphertext);
        BitSet plaintext = decrypt(bitSet);
        return bitSetToString(plaintext);
    }

    public BitSet encrypt(BitSet plaintext) {
        generateSubKeys();

        BitSet permutedPlaintext = permute(plaintext, IP);
        BitSet fkResult = fk(permutedPlaintext, subKey1);
        BitSet swapped = swapBits(fkResult);
        BitSet fkResult2 = fk(swapped, subKey2);
        BitSet ciphertext = permute(fkResult2, IP_INVERSE);

        return ciphertext;
    }

    

    public BitSet decrypt(BitSet ciphertext) {
        generateSubKeys();

        BitSet permutedCiphertext = permute(ciphertext, IP);
        BitSet fkResult = fk(permutedCiphertext, subKey2);
        BitSet swapped = swapBits(fkResult);
        BitSet fkResult2 = fk(swapped, subKey1);
        BitSet plaintext = permute(fkResult2, IP_INVERSE);

        return plaintext;
    }

    public void setKey(BitSet key) {
        if (key.length() != 10) {
            throw new IllegalArgumentException("Invalid key length. Key must be 10 bits long.");
        }
        this.key = key;
    }

    private void generateSubKeys() {
        BitSet permutedKey = permute(key, P10);
        BitSet leftKey = leftShift(permutedKey, 1);
        BitSet rightKey = leftShift(permutedKey, 2);
        subKey1 = permute(leftKey, P8);
        subKey2 = permute(rightKey, P8);
    }

    private BitSet permute(BitSet input, int[] permutationTable) {
        BitSet output = new BitSet(permutationTable.length);
        for (int i = 0; i < permutationTable.length; i++) {
            output.set(i, input.get(permutationTable[i] - 1));
        }
        return output;
    }

    private BitSet leftShift(BitSet input, int count) {
        BitSet output = (BitSet) input.clone();
        output.clear(0, count);
        for (int i = input.length() - count; i < input.length(); i++) {
            output.set(i, input.get(i - input.length() + count));
        }
        return output;
    }

    private BitSet fk(BitSet input, BitSet subKey) {
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
        int row1 = getDecimalValue(left.get(0), left.get(3));
        int col1 = getDecimalValue(left.get(1), left.get(2));
        int sBox1Value = S0[row1][col1];

        int row2 = getDecimalValue(right.get(0), right.get(3));
        int col2 = getDecimalValue(right.get(1), right.get(2));
        int sBox2Value = S1[row2][col2];

        BitSet output = new BitSet(4);
        output.set(0, (sBox1Value & 0b10) != 0);
        output.set(1, (sBox1Value & 0b01) != 0);
        output.set(2, (sBox2Value & 0b10) != 0);
        output.set(3, (sBox2Value & 0b01) != 0);

        return output;
    }

    private int getDecimalValue(boolean bit1, boolean bit2) {
        int value = 0;
        if (bit1) value += 2;
        if (bit2) value += 1;
        return value;
    }

    private BitSet swapBits(BitSet input) {
        BitSet output = new BitSet(input.length());
        for (int i = 0; i < input.length(); i++) {
            output.set(i, input.get(i + (input.length() / 2)));
        }
        for (int i = input.length() / 2; i < input.length(); i++) {
            output.set(i, input.get(i - (input.length() / 2)));
        }
        return output;
    }
    private static BitSet stringToBitSet(String text) {
        BitSet bitSet = new BitSet(text.length() * 8);
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            for (int j = 0; j < 8; j++) {
                bitSet.set(i * 8 + j, ((c >> (7 - j)) & 1) == 1);
            }
        }
        System.out.println("stringToBitSet = " + bitSet);
        return bitSet;
    }



    private static String bitSetToString(BitSet bitSet) {
        int byteArraySize = (bitSet.length() + 7) / 8;
        byte[] byteArray = new byte[byteArraySize];
        for (int i = 0; i < bitSet.length(); i++) {
            if (bitSet.get(i)) {
                int byteIndex = i / 8;
                int bitIndex = 7 - (i % 8);
                byteArray[byteIndex] |= (1 << bitIndex);
            }
        }
        String str = new String(byteArray);
        System.out.println("String: " + str);
        System.out.println("bitSetToString = " + Arrays.toString(byteArray));
        return new String(byteArray);
    }

}
