package com.example.cryptchat.Crypto;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;

import androidx.annotation.RequiresApi;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.Random;
    // containing the corresponding RSA public key.

public class RSASignVerify {

    /*private PrivateKey privateKey;   // RSA private key
    private PublicKey publicKey;   // RSA private key
    private Certificate certificate  = new Cert; // Certificate chain with the first certificate

    public void generate() throws NoSuchAlgorithmException
    {
        //generare pereche de chei
        KeyPairGenerator kpg=KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp=kpg.genKeyPair();
        publicKey = kp.getPublic();
        privateKey = kp.getPrivate();
    }

    private KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
    keyStore.load(null);
    keyStore.setEntry(
         "key2",
         new KeyStore.PrivateKeyEntry(privateKey, certChain),
            new KeyProtection.Builder(KeyProperties.PURPOSE_SIGN)
            .setDigests(KeyProperties.DIGEST_SHA256)
                 .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
    // Only permit this key to be used if the user
    // authenticated within the last ten minutes.
                 .setUserAuthenticationRequired(true)
                 .setUserAuthenticationValidityDurationSeconds(10 * 60)
                 .build());
    // Key pair imported, obtain a reference to it.
    PrivateKey keyStorePrivateKey = (PrivateKey) keyStore.getKey("key2", null);
    PublicKey publicKey = keyStore.getCertificate("key2").getPublicKey();
    // The original private key can now be discarded.

    Signature signature = Signature.getInstance("SHA256withRSA");

    public RSASignVerify() throws KeyStoreException {
    }
 signature.initSign(keyStorePrivateKey);*/


}