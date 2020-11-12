package com.example.cryptchat.Crypto;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;
import android.util.JsonWriter;

import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.spongycastle.cert.jcajce.JcaX509v3CertificateBuilder;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import javax.security.auth.x500.X500Principal;

// To create a certificate chain we need the issuers' certificate and private key. Keep these together to pass around

@RequiresApi(api = Build.VERSION_CODES.M)
public
class GeneratedCert {

/*    private FirebaseAuth auth;
    private static FirebaseUser firebaseUser = auth.getCurrentUser();
    private String userid = firebaseUser.getUid();
    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(userid);*/


/*  Salvarea privateKey si publicKey se face in formatele corespunzatoare,
     privateKey -> PKCS#8
     certificate.getPublicKey() -> X.509
    dar trebuie creati getteri pentru chei si refactorizat codul pt generarea cheilor-getteri (+model boriga +net) */

/*    private static PrivateKey privateKey;
    private static PublicKey publicKey;*/
    private final static String ANDROID_KEY_STORE = "AndroidKeyStore";

    public final static int ANY_PURPOSE = KeyProperties.PURPOSE_ENCRYPT |
            KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_SIGN |
            KeyProperties.PURPOSE_VERIFY;

    public final static long CENTURY = (100 * 365) + 24;

    public enum KeySize
    {
        BIT_512  ( 512),
        BIT_768  ( 768),
        BIT_1024 (1024),
        BIT_2048 (2048),
        BIT_3072 (3072),
        BIT_4096 (4096);

        private int value;
        KeySize(int value)
        {
            this.value = value;
        }

        public int value()
        {
            return this.value;
        }
    }

    public enum SignatureAlgorithm
    {
        MD5WithRSA        ("MD5WithRSA"),
        SHA1WithRSA       ("SHA1WithRSA"),
        SHA1WithRSA_PSS   ("SHA1WithRSA/PSS"),
        SHA224WithRSA     ("SHA224WithRSA"),
        SHA224WithRSA_PSS ("SHA224WithRSA/PSS"),
        SHA256WithRSA     ("SHA256WithRSA"),
        SHA256WithRSA_PSS ("SHA256WithRSA/PSS"),
        SHA384WithRSA     ("SHA384WithRSA"),
        SHA384WithRSA_PSS ("SHA384WithRSA/PSS"),
        SHA512WithRSA     ("SHA512WithRSA"),
        SHA512WithRSA_PSS ("SHA512WithRSA/PSS");

        private String value;

        SignatureAlgorithm(String value)
        {
            this.value = value;
        }

        public String value()
        {
            return this.value;
        }
    }

/*    public GeneratedCert(PrivateKey privateKey, PublicKey publicKey, X509Certificate certificate) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.certificate = certificate;
    }*/

/*    public static PublicKey generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        KeySize keySize = null;
        kpg.initialize(2048);
        KeyPair keyPair = kpg.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
        System.out.println("~?~?~?~?~?~?~?~?~?~?~?~?~?~?~?");
        System.out.println("\nCheie privata: " + privateKey + "\nCheie publica: " + publicKey + "\n");
        *//* System.out.println("\nCheie privata ToString:" + privateKey.toString() + "\nCheie publica ToString: " + publicKey.toString() + "\n");
        System.out.println("\nCheie privata Format:" + privateKey.getFormat() + "\nCheie publica Format: " + publicKey.getFormat() + "\n");
        System.out.println("\nCheie privata Encoded:" + Arrays.toString(privateKey.getEncoded()) + "\nCheie publica Encoded: " + Arrays.toString(publicKey.getEncoded()) + "\n");
        System.out.println("\nCheie privata Algorithm:" + privateKey.getAlgorithm() + "\nCheie publica Algorithm: " + publicKey.getAlgorithm() + "\n");*//*
        System.out.println("~?~?~?~?~?~?~?~?~?~?~?~?~?~?~?");
        return publicKey;
    }*/

    public static KeyPair init(final String alias, final GeneratedCert.KeySize keySize,
                               final long validityDays, final Boolean reset)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
            IOException, NoSuchProviderException, InvalidAlgorithmParameterException
    {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
        if (reset || (!keyStore.containsAlias(alias)))
        {
            final long now = java.lang.System.currentTimeMillis();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEY_STORE);
            keyPairGenerator.initialize(
                    new KeyGenParameterSpec.Builder(alias, GeneratedCert.ANY_PURPOSE)
                            .setRandomizedEncryptionRequired(false)
                            .setDigests(
                                    KeyProperties.DIGEST_NONE,   KeyProperties.DIGEST_MD5,
                                    KeyProperties.DIGEST_SHA1,   KeyProperties.DIGEST_SHA224,
                                    KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA384,
                                    KeyProperties.DIGEST_SHA512)
                            .setKeySize(keySize.value())
                            .setEncryptionPaddings(
                                    KeyProperties.ENCRYPTION_PADDING_NONE,
                                    KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1,
                                    KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                            .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                            .setCertificateSubject(new X500Principal(
                                    "CN=Android, O=Android Authority"))
                            .setCertificateSerialNumber(new BigInteger(256, new Random()))
                            .setCertificateNotBefore(new Date (now - (now % 1000L)))
                            .setCertificateNotAfter(new Date(((new Date(now - (now % 1000L)))
                                    .getTime()) + (validityDays * 86400000L)))
                            .build());
            return keyPairGenerator.generateKeyPair();
        }
        else return null;
    }

    public static PublicKey publicKey(final String alias)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
            IOException, UnrecoverableEntryException
    {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
        return keyStore.getCertificate(alias).getPublicKey();
    }


               /* CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(publicKeyBytes);
                String aka = "alias";//cert.getSubjectX500Principal().getName();

                KeyStore trustStore = KeyStore.getInstance(ANDROID_KEY_STORE);
                trustStore.load(null);
                trustStore.setCertificateEntry(aka, cert);*/


/*
                KeyPairGenerator kpg=KeyPairGenerator.getInstance("RSA");
                kpg.initialize(1024);
                KeyPair kp = kpg.genKeyPair();
                PublicKey publick = kp.getPublic();
                PrivateKey privateKey = kp.getPrivate();*/


       /*         X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PublicKey bobPubKey = keyFactory.generatePublic(publicKeySpec);*/

               /* byte[] publicKeyBytes = new byte[0];
                X509EncodedKeySpec kspec = new X509EncodedKeySpec(publicKey.getEncoded());
                X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
                System.out.println("Public Key: " + keyFactory.generatePublic(publicKeySpec).getAlgorithm());*/

              /*  System.out.println("Public Key 509: " + bobPubKey.getFormat());

                return keyFactory.generatePublic(publicKeySpec);*/


    public static PrivateKey privateKey(final String alias)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
            IOException, UnrecoverableEntryException
    {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
        return (PrivateKey) keyStore.getKey(alias, null);
    }

       /* KeyPairGenerator kpg=KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        KeyPair kp = kpg.genKeyPair();
        PrivateKey privateKey = kp.getPrivate();*/

/*        PKCS8EncodedKeySpec kspec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey
        System.out.println("Private Key 509: " + privateKey.getFormat());

        return kf.generatePrivate(kspec);
    }*/

    public static byte[] sign(final String message, final String alias,
                              final SignatureAlgorithm algorithm)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
            IOException, UnrecoverableEntryException,
            InvalidKeyException, SignatureException
    {
        Signature instance = Signature.getInstance(algorithm.value());
        instance.initSign(privateKey(alias), new SecureRandom());
        instance.update(message.getBytes("UTF-8"));
        return instance.sign();
    }


    public static Boolean verify(final String message, final byte[] signature,
                                 final String alias, final SignatureAlgorithm algorithm)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
            IOException, UnrecoverableEntryException,
            InvalidKeyException, SignatureException
    {
        Signature instance = Signature.getInstance(algorithm.value());
        instance.initVerify(publicKey(alias));
        instance.update(message.getBytes("UTF-8"));
        return instance.verify(signature);
    }

}




/*     public void generatePublicKey() {

         // generating a public key using X.509 standard and saving it into the Firebase DB
         X509EncodedKeySpec publicKeyX509 = new X509EncodedKeySpec(publicKey.getEncoded());

         HashMap<String, Object> keyHashMap = new HashMap<>();
//         keyHashMap.put("publicKey", publicKeyX509);
//         keyHashMap.put("publicKeyToString", publicKeyX509.toString());
         keyHashMap.put("publicKeyEncoded", publicKeyX509.getEncoded()); // -> ASTA PARE SA FIE FORMATUL CORECT

         myRef.setValue(keyHashMap);

         System.out.println("\npublicKeyX509: " + publicKeyX509);
         System.out.println("\npublicKeyX509 toString: " + publicKeyX509.toString());
         System.out.println("\npublicKeyX509 Encoded: " + publicKeyX509.getEncoded().toString());

     }*/



     /*@RequiresApi(api = Build.VERSION_CODES.M)
     public void generatePrivateKey() throws NoSuchAlgorithmException, InvalidKeyException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException {

         PKCS8EncodedKeySpec privateKeyPKCS8 = new PKCS8EncodedKeySpec(privateKey.getEncoded());
         // generating a private key using PKCS8 standard and saving it locally on the phone storage

         GeneratedCert gen = new GeneratedCert();

         KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
         keyStore.load(null);
         keyStore.setEntry(
                 "key2",
                 new KeyStore.PrivateKeyEntry(privateKey, gen),
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
         signature.initSign(keyStorePrivateKey);
         System.out.println("\nprivateKeyPKCS8 toString: " + privateKeyPKCS8.toString());
         System.out.println("\nprivateKeyPKCS8 Encoded: " + Arrays.toString(privateKeyPKCS8.getEncoded()));

     }*/



    /**
     * @param cnName The CN={name} of the certificate. When the certificate is for a domain it should be the domain name
     * @param domain Nullable. The DNS domain for the certificate.
     * @param issuer Issuer who signs this certificate. Null for a self-signed certificate
     * @param isCA   Can this certificate be used to sign other certificates
     * @return Newly created certificate with its private key
     */
   /* @RequiresApi(api = Build.VERSION_CODES.O)
    public static GeneratedCert createCertificate(String cnName, String domain, GeneratedCert issuer, boolean isCA) throws Exception {
        // Generate the key-pair with the official Java API's
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        KeyPair certKeyPair = keyGen.generateKeyPair();
        PrivateKey privateKey;
        PublicKey publicKey;
        privateKey = certKeyPair.getPrivate();
        publicKey = certKeyPair.getPublic();
        X500Name name = new X500Name("CN=" + cnName);
        // If you issue more than just test certificates, you might want a decent serial number schema ^.^
        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
        Instant validFrom = Instant.now();
        Instant validUntil = validFrom.plus(10 * 360, ChronoUnit.DAYS);

        // If there is no issuer, we self-sign our certificate.
        X500Name issuerName;
        PrivateKey issuerKey;
        if (issuer == null) {
            issuerName = name;
            issuerKey = certKeyPair.getPrivate();
        } else {
            issuerName = new X500Name(issuer.certificate.getSubjectDN().getName());
            issuerKey = issuer.privateKey;
        }

        // The cert builder to build up our certificate information
        JcaX509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
                issuerName,
                serialNumber,
                from(validFrom), from(validUntil),
                name, certKeyPair.getPublic());

        // Make the cert to a Cert Authority to sign more certs when needed
        if (isCA) {
            builder.addExtension(Extension.basicConstraints, true, new BasicConstraints(isCA));
        }
        // Modern browsers demand the DNS name entry
        if (domain != null) {
            builder.addExtension(Extension.subjectAlternativeName, false,
                    new GeneralNames(new GeneralName(GeneralName.dNSName, domain)));
        }

        // Finally, sign the certificate:
        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSA").build(issuerKey);
        X509CertificateHolder certHolder = builder.build(signer);
        X509Certificate cert = new JcaX509CertificateConverter().getCertificate(certHolder);

        return new GeneratedCert(certKeyPair.getPrivate(), cert);
    }*/

