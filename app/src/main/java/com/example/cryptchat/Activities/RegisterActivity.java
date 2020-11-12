package com.example.cryptchat.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cryptchat.Model.ECHDUser;
import com.example.cryptchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;

import static org.spongycastle.pqc.math.linearalgebra.ByteUtils.toHexString;

public class RegisterActivity extends AppCompatActivity {

    // Widgets
    EditText userET, passET, emailET;
    Button registerBtn;
    TextView textLoginRedirect;


    // Firebase
    FirebaseAuth auth;
    DatabaseReference myRef;

    PrivateKey privateKey;
    PublicKey publicKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initializing Widgets:
        userET = findViewById(R.id.userEditText);
        passET = findViewById(R.id.loginPassEditText);
        emailET = findViewById(R.id.loginEmailEditText);
        registerBtn = findViewById(R.id.buttonRegister);
        textLoginRedirect = findViewById(R.id.loginExAcc);


        // Firebase initialisations
        auth = FirebaseAuth.getInstance();

        // Adding Event Listener to Button Registration
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username_text = userET.getText().toString();
                String email_text = emailET.getText().toString();
                String pass_text = passET.getText().toString();

                if (TextUtils.isEmpty(username_text) || TextUtils.isEmpty(email_text) || TextUtils.isEmpty(pass_text)) {
                    Toast.makeText(RegisterActivity.this, "Please fill in all the fields!", Toast.LENGTH_SHORT).show();

                } else {
                    RegisterNow(username_text, email_text, pass_text);
                }
            }
        });

        textLoginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

    }

    public PublicKey generateKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {

        PublicKey publicKey1;

        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec ecsp = new ECGenParameterSpec("secp256r1");
        keyPairGen.initialize(ecsp);

        // We create two ECDH users, Alice and Bob. Their public keys will print uponing running this code.

        ECHDUser charlie = new ECHDUser("Alice", keyPairGen);
        publicKey1 = charlie.getPubKey();
        System.out.println(charlie.getName() + "’s public key is: " + publicKey1);

       /* KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec ecsp = new ECGenParameterSpec("secp256r1");
        keyPairGen.initialize(ecsp);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();*/
        System.out.println("~?~?~?~?~?~?~?~?~?~?~?~?~?~?~?");
//        System.out.println("\nCheie privata: " + privateKey + "\nCheie publica: " + publicKey + "\n");
        /*
        // other S.o.u.t's

        System.out.println("\nCheie privata ToString:" + privateKey.toString() + "\nCheie publica ToString: " + publicKey.toString() + "\n");
        System.out.println("\nCheie privata Format:" + privateKey.getFormat() + "\nCheie publica Format: " + publicKey.getFormat() + "\n");
        System.out.println("\nCheie privata Encoded:" + Arrays.toString(privateKey.getEncoded()) + "\nCheie publica Encoded: " + Arrays.toString(publicKey.getEncoded()) + "\n");
        System.out.println("\nCheie privata Algorithm:" + privateKey.getAlgorithm() + "\nCheie publica Algorithm: " + publicKey.getAlgorithm() + "\n");
        */
        System.out.println("~?~?~?~?~?~?~?~?~?~?~?~?~?~?~?");
        return publicKey1;
    }

    // Implementing the Register Functionality
    private void RegisterNow(final String username, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            String userid = firebaseUser.getUid();

                            myRef = FirebaseDatabase.getInstance().getReference("MyUsers")
                                    .child(userid);

                            // Generating a Self-Signed X509Certificate for every new user that is registering

                            // last try encoded
                            /*
                            try {
                                generateKeyPair();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }
                            X509EncodedKeySpec publicKeyX509 = new X509EncodedKeySpec(publicKey.getEncoded());

                            byte[] publicKeyEncoded = publicKeyX509.getEncoded();
                            */

                           /* // - ENCODED KEY -
                            try {
                                generateKeyPair();
                            } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
                                e.printStackTrace();
                            }
                            X509EncodedKeySpec cheiePublicaX509 = new X509EncodedKeySpec(publicKey.getEncoded());

                            System.out.println("\ncheiePublicax509: " + cheiePublicaX509);
*/
                            //java.security.spec.X509EncodedKeySpec@6a9349a
//                          System.out.println("\n Cheie publica:  " + cheiePublicaX509);
//                          System.out.println("\n Cheie publica toString:  " + cheiePublicaX509.toString()); // la fel

                            // [B@-something else every time
//                          System.out.println("\n cheiePublicaX509.getEncoded(): " + cheiePublicaX509.getEncoded());

                           /* // [B@340055e
                            String cheiePublicaX509toString = cheiePublicaX509.getEncoded().toString(); // -> ASTA PARE SA FIE FORMATUL CORECT
                            System.out.println("\n HEX: toHexString(cheiePublicaX509.getEncoded()) " + toHexString(cheiePublicaX509.getEncoded()));
                            System.out.println("\n cheiePublicaX509.getEncoded().toString: " + cheiePublicaX509toString);

                            // W0JAMzQwMDU1ZQ==
                            String encodedKey = Base64.getEncoder().encodeToString(cheiePublicaX509toString.getBytes());
                            System.out.println("\n Encoded key: " + encodedKey);

                            byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
                            String decodedString = new String(decodedKey);
                            // [B@8991d3f
                            System.out.println("\n Decoded key: " + decodedKey);
//                          System.out.println("\n Decoded key To String: " + decodedKey.toString()); // la fel
                            // [91, 66, 64, 51, 52, 48, 48, 53, 53, 101]
                            System.out.println("\n Arrays.toString(decodedKey): " + Arrays.toString(decodedKey));
                            // [B@340055e
                            System.out.println("\n Decoded String key: " + decodedString);

                            String stringEncodedBack = Base64.getEncoder().encodeToString(decodedString.getBytes());
                            // W0JAMzQwMDU1ZQ==
                            System.out.println("\n Encoded String Back: " + stringEncodedBack);

                            System.out.println("\n~?~?~?~?~?~?~?~?~?~?~?~?~?~?~?");

//                              System.out.println("\n cheiePublicaX509.getEncoded().toString(): " + cheiePublicaX509.getEncoded().toString());
                            System.out.println("\n Format Cheie: " + cheiePublicaX509.getFormat());*/


                            HashMap<String, Object> hashMap = new HashMap<>();
                            try {

                            // HashMaps
                            hashMap.put("id", userid);
                            hashMap.put("username", username);
                            hashMap.put("imageURL", "default");
                            hashMap.put("status", "offline");
//                                hashMap.put("publicKey", generateKeyPair());
//                                hashMap.put("publicKey", generateKeyPair().getEncoded().toString()); //test10pk
                                hashMap.put("publicKey", toHexString(generateKeyPair().getEncoded())); // test 11 pk
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (InvalidAlgorithmParameterException e) {
                                e.printStackTrace();
                            }



//                            hashMap.put("cheiePublicaX509toString", cheiePublicaX509toString);


                            // Opening the Main Activity after Successful Registration
                            myRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(RegisterActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

}
