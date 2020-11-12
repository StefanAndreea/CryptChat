package com.example.cryptchat.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.cryptchat.Crypto.GeneratedCert;
import com.example.cryptchat.Fragments.ChatsFragment;
import com.example.cryptchat.Fragments.ProfileFragment;
import com.example.cryptchat.Model.Users;
import com.example.cryptchat.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.spongycastle.util.encoders.Hex;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;

/*
 */

/* !! IT'S A MUST !!*/
/* SECURITATE & CRIPTARE */
//TODO - cheie de sesiune de fiecare data cand se incepe o conversatie => dupa ce s-a trimis un mesaj
//TODO - de implementat functionalitatea Snap de delete messages dupa ce userul le-a vazut
// hint: MessageActivity.java, linia 148

/* FUNCTIONALITIES */
//TODO - cand se creaza un nou user, app da crash pt ca nu gaseste userul (java.lang.NullPointerException: Attempt to invoke virtual method 'boolean java.lang.String.equals(java.lang.Object)' on a null object reference
//        at com.example.cryptchat.Fragments.UsersFragment$1.onDataChange(UsersFragment.java:73))
//TODO - de pus buton de back to chats din conversatie
//TODO - de implementat function de add friend a.i. sa NU se vada TOTI USERII la Users (+cert/RSA?)


/* ! AFTER I SEND THE MAIL !*/
/* AESTHETICS */
//TODO - de facut thumbnail images mai mici si rotunde (peste tot sau unde se poate)
//TODO - de facut status online / offline rotund + text verde / rosu
// (https://stackoverflow.com/questions/22105775/imageview-in-circular-through-xml)
//TODO - dupa handshake-ul RSA, userii participanti la discutie sa primeasca o modala de confirmare

/* CUSTOMIZING */
//TODO - customizing the Toast messages for Registering & Login
//TODO - de stabilit un UI / UX atragator/simplist - cautat palete de culori (+ fonts ?)

/* ! only if I have time OR it affects the performance !*/
/* WARNINGS diff */
//TODO - Build Note: ProfileFragment.java uses unchecked or unsafe operations.
//TODO - I/Choreographer: Skipped 47 frames!  The application may be doing too much work on its main thread.
//TODO - de cautat & rezolvat eroarea: Emulator: WARNING: EmulatorService.cpp:448:
// Cannot find certfile: C:\Users\Becky\.android\emulator-grpc.cer security will be disabled.
//TODO - de cautat & rezolvat eroarea E/SpannableStringBuilder: SPAN_EXCLUSIVE_EXCLUSIVE spans cannot have a zero length
//TODO - de cautat & rezolvat eroarea (doar pe tel fizic): W/ConnectionTracker: Exception thrown while unbinding
//  java.lang.IllegalArgumentException: Service not registered: lq@ecfe443
//TODO - de cautat & rezolvat eroarea E/StudioProfiler: JVMTI error: 103(JVMTI_ERROR_ILLEGAL_ARGUMENT)
//TODO - de cautat & rezolvat eroarea E/StudioProfiler: JVMTI error: 15(JVMTI_ERROR_THREAD_NOT_ALIVE)
//TODO - de cautat & rezolvat eroarea tchat W/InputMethodManager: InputMethodManager.getInstance() is deprecated
// because it cannot be compatible with multi-display. Use context.getSystemService(InputMethodManager.class) instead.
//TODO - W/Glide: Failed to find GeneratedAppGlideModule. You should include an annotationProcessor
//  compile dependency on com.github.bumptech.glide:compiler in your application and a
//  @GlideModule annotated AppGlideModule implementation or LibraryGlideModules will be silently ignored

/*
COMPLETED TO-DO'S
 */
//DONE: 5TODO - la crearea unui user, se insereaza in baza de date numai statusul. de verificat
// hashMap-urile, modelul Users, Fragments si RegisterActivity
// REASON: The Firebase Database has to have full hashMap inserated (id, username, imageURL, status),
// not only the ID -> TO DELETE any entry that containts only the ID.
// WHY: no code has to be between hashMaps put's and the addValueEventListener (?? or so it seems)
//DONE: 4TODO - de pus separator intre bottom si recyclerView (activity_message.xml)
//DONE: 3TODO - de aranjat layout-ul pentru utiliz 2, daca s-au trimis deja mesaje de la celalalt utiliz-de verificat Emulator & tel fizic
//DONE: 2TODO - MESAGERIA NU ESTE IN TIMP REAL, EXISTA 1 MESAJ DELAY DE PE SERVER!! -> de investigat
//DONE: 1TODO - de putut da click si pe numele utilizatorului pentru a-i trimite mesaj, nu doar pe poza


@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {

    // Firebase
    FirebaseUser firebaseUser;
    DatabaseReference myRef;

    PrivateKey privateKey;
    PublicKey publicKey;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fresco.initialize(this);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(firebaseUser.getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Users user = dataSnapshot.getValue(Users.class);
                assert user != null;


//                System.out.println("USERINO:" + user.getcheiePublicaX509toString() + "\n");

                /*try {
                    generateKeyPair();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                X509EncodedKeySpec cheiePublicaX509 = new X509EncodedKeySpec(publicKey.getEncoded());*/

                //java.security.spec.X509EncodedKeySpec@6a9349a
//                          System.out.println("\n Cheie publica:  " + cheiePublicaX509);
//                          System.out.println("\n Cheie publica toString:  " + cheiePublicaX509.toString()); // la fel

                // [B@-something else every time
//                          System.out.println("\n cheiePublicaX509.getEncoded(): " + cheiePublicaX509.getEncoded());

                // [B@340055e
//                String cheiePublicaX509toString = cheiePublicaX509.getEncoded().toString();
//                System.out.println("\n HEX: toHexString(cheiePublicaX509.getEncoded()) " + toHexString(cheiePublicaX509.getEncoded()));
//                System.out.println("\n cheiePublicaX509.getEncoded().toString: " + cheiePublicaX509toString);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        /* INCERCARI CRIPTARI */

        // privateKey -> PRIVATE KEY FORMAT: PKCS#8 (local pe telefon)
        // certificate.getPublicKey() -> PUBLIC KEY FORMAT: X.509 (in baza de date)

        // merge dar pune string-ul intr-o ramura mai adanca decat userul respectiv
        /*X509Certificate certificate = null;
        try {
            certificate = GeneratedCert.createCertificate("https://cryptchat-f0476.firebaseio.com",
                    null, null, false).certificate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert certificate != null;
        PublicKey publicKey = certificate.getPublicKey();
        System.out.println("PUBLIC KEY : " + publicKey + "\n");
        System.out.println("PUBLIC KEY ENCODED : " + publicKey.getEncoded() + "\n");

        HashMap<String, Object> keyHashMap = new HashMap<>();
        keyHashMap.put("publicKey", publicKey.toString());
        keyHashMap.put("publicKeyEncoded", publicKey.getEncoded().toString());
        myRef.push().setValue(keyHashMap);   <<- din cauza asta creaza inca un copil / o ramura */

        // Instantiate TabLayout and ViewPager
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.view_pager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new ChatsFragment(), "Chats");
//        viewPagerAdapter.addFragment(new UsersFragment(), "Contacts");
        viewPagerAdapter.addFragment(new ProfileFragment(), "Profile");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }


/*    public void generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        KeyPair keyPair = generator.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
        System.out.println("\n~?~?~?~?~?~?~?~?~?~?~?~?~?~?~?");
        System.out.println("\nCheie privata: " + privateKey + "\nCheie publica: " + publicKey + "\n");
        // other S.o.u.t's
        *//*
        System.out.println("\nCheie privata ToString:" + privateKey.toString() + "\nCheie publica ToString: " + publicKey.toString() + "\n");
        System.out.println("\nCheie privata Format:" + privateKey.getFormat() + "\nCheie publica Format: " + publicKey.getFormat() + "\n");
        System.out.println("\nCheie privata Encoded:" + Arrays.toString(privateKey.getEncoded()) + "\nCheie publica Encoded: " + Arrays.toString(publicKey.getEncoded()) + "\n");
        System.out.println("\nCheie privata Algorithm:" + privateKey.getAlgorithm() + "\nCheie publica Algorithm: " + publicKey.getAlgorithm() + "\n");
        *//*
        System.out.println("~?~?~?~?~?~?~?~?~?~?~?~?~?~?~?");
    }*/


    // Adding Logout functionality
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
        }
        return false;
    }

    // Adding Functionality for TabLayout and ViewPager
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

    }

    private void CheckStatus(String status) {
        myRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        myRef.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        CheckStatus("offline");
    }
}
