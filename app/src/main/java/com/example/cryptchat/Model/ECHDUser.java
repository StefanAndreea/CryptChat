package com.example.cryptchat.Model;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;

import static org.spongycastle.pqc.math.linearalgebra.ByteUtils.toHexString;

public class ECHDUser {

    /** This ECDH implementer’s private key. */
    private PrivateKey privKey;
    /** This ECDH implementer’s public key. */
    private PublicKey pubKey;
    /** The name of this ECDH User. */
    private String name;

    // Firebase
    FirebaseUser firebaseUser;
    DatabaseReference myRef;

    public ECHDUser(String name, KeyPairGenerator keyPairGen) {
        this.name = name;
        KeyPair keyPair = keyPairGen.generateKeyPair();
        this.privKey = keyPair.getPrivate();
        this.pubKey = keyPair.getPublic();
    }

    public PublicKey getPubKey() {


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(firebaseUser.getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Users user = dataSnapshot.getValue(Users.class);

                user.setPublicKey(toHexString(pubKey.getEncoded()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

//        System.out.println("\nFormat cheie publica: " + pubKey.getFormat());
//        System.out.println("\nCheia publica: " + pubKey);

        return pubKey;

    }


        public String getName() {
            return name;
    }

    public byte[] sendValueTo(ECHDUser receiver) throws Exception {

        // Get A key agreement controller for ECDH
        KeyAgreement ecdh = KeyAgreement.getInstance("ECDH");
        // Initialize it so it knows the private key
        ecdh.init(this.privKey);
//        System.out.println("\nFormat cheie privata: " + privKey.getFormat());


        byte[] publicKeyEncoded = receiver.getPubKey().getEncoded();

        KeyFactory keyFactory = KeyFactory.getInstance("EC");

        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyEncoded);

        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);

//        System.out.println("public Key to String: " + publicKey.toString());

        // DoPhase actually computes the shared value A^b = B^a
        // The second parameter lets the ECDH instance know that we are
        // all done using this instance
        ecdh.doPhase(publicKey, true);

        byte[] sharedSecret = ecdh.generateSecret();

//        System.out.println(receiver.getName() + "'s secret : " + toHexString(sharedSecret));

        return sharedSecret;

        }

}
