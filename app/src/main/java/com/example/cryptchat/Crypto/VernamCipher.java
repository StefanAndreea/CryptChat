package com.example.cryptchat.Crypto;

import static org.spongycastle.pqc.math.linearalgebra.ByteUtils.toHexString;

public class VernamCipher {

    private String text1;
    private String key;
    private String text2;

    public VernamCipher() {}

    public VernamCipher(String text1, String key, String text2) {
        this.text1 = text1;
        this.key = key;
        this.text2 = text2;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    /* Checking if the key has the same length as the plain text
	and fills the key to have the same length as the plain text. */
    protected String check(String givenText, String keyPadded)
    {
        givenText = givenText.toLowerCase();
        keyPadded = keyPadded.toLowerCase();
        int k = 0;
        System.out.println("\nThe Key length before: " + keyPadded.length());
        if (givenText.length() != keyPadded.length()) {
            while (givenText.length() != keyPadded.length()) {

                if(givenText.length() > keyPadded.length()) {
                    keyPadded += keyPadded.charAt(k);
                    k++;
                }
                else {
                    keyPadded = keyPadded.substring(0, keyPadded.length()-1);
                }

            }
        }
        System.out.println("\nThe Key: " + keyPadded);
        System.out.println("\nThe Key length after: " + (keyPadded.length()));
        return keyPadded;
    }


    public String encryption(String plainText, String key, String cipherText)
    {
        char[] plainTextChar = plainText.toCharArray();

//      Checking the plaintext and the key
        key = check(plainText, key);
        char[] keyChar = key.toCharArray();

        int length = key.length();
//		System.out.print("\nLength: " + length);

        char[] cipher = new char[length];

        // Encrypt the text by using XOR (exclusive OR) each character of our text against cipher. */
        System.out.println("\nEnciphered text is gonna be... ");
        for (int i = 0; i < length; i++) {
            cipher[i] = (char) (plainTextChar[i] ^ keyChar[i]);
//            System.out.println(cipher[i]);

            cipherText = cipherText + cipher[i];
            System.out.print(cipherText);
        }

        return cipherText;
    }

    public String decryption(String cipherText, String key, String plainText)
    {
        char[] cipherTextChar = cipherText.toCharArray();

//      Checking the cipherText and the key
        key = check(cipherText, key);
        char[] keyChar = key.toCharArray();

        int length = key.length();
//		System.out.print("\nLength: " + length);

        char[] decipher = new char[length];

        System.out.println("\nDeciphered is gonna be... ");
        // Run through the encrypted text and against the cipher again and decrypts the text.
        for (int i = 0; i < length; i++) {
            decipher[i] = (char) (cipherTextChar[i] ^ keyChar[i]);
//			System.out.print(decipher[i]);

            plainText = plainText + decipher[i];
            System.out.print(plainText);
        }

        return plainText;

    }

    public String shareKeyAcrossUsers()
    {
        DHSharingSecret secret = new DHSharingSecret();

        byte[] sharedSecret = new byte[0];
        try {
            sharedSecret = secret.shareSecret();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String xorSeed = toHexString(sharedSecret);
        System.out.print("\nShared secret: " + xorSeed);

        System.out.println("\nTextul criptat este: ");
        long seed = seedPadding(xorSeed);
        XORShift rand = new XORShift(seed);

        // String plainText="The xorshift generator is among the fastest non-cryptographically-secure random number generators.";
//        String cipherText = "";

        String VernamKey = String.valueOf(rand.nextLong(9223720368547L));

        System.out.print("\n Vernam Key: " + VernamKey);

        return VernamKey;

    }

    public static long seedPadding(String string) {

        long seedXOR = 0;
        // extracting only the digits from the shared secret
        for (int i = 0; i < string.length(); i++) {
            string = string.replaceAll("\\D+", "");
        }

        long length = (string.length() / 2);

        string = string.substring(0, (int) length);

        System.out.print("\n THE SEED PADDED: " + string);

        char[] charString = string.toCharArray();

        XORShift rand = new XORShift();

        for (int i = 0; i < length; i++) {
            int randomIndexToSwap = (int) rand.nextLong(length);
            char temp = charString[randomIndexToSwap];
            charString[randomIndexToSwap] = charString[i];
            charString[i] = temp;
            seedXOR = seedXOR * 10 + charString[i];
        }
        System.out.println("\nThe seed shuffled");
//		System.out.println(String.valueOf(charString));
//		System.out.println("SEEDXOR");
        System.out.println(seedXOR);

        return seedXOR;
    }


}
