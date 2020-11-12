package com.example.cryptchat.Crypto;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.cryptchat.Model.Chat;
import com.example.cryptchat.Model.ECHDUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.KeyPairGenerator;
import java.security.spec.ECGenParameterSpec;

public class DHSharingSecret {

    public DHSharingSecret() {}

    FirebaseUser user1;
    String userid;

    // Each user is generating a DH key pair with ?? (1024)-bit key size
    public byte[] shareSecret() throws Exception {

        /* Initialize the EC using "secp256r1" curve specifications.
        This just defines a set of parameters where the key size
        is 233 bits and the parameters are chosen (somewhat)
        randomly (hence the r1). */
        final KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec ecsp = new ECGenParameterSpec("secp256r1");
        keyPairGen.initialize(ecsp);

        // We create two ECDH users, Alice and Bob. Their public keys will print uponing running this code.
        user1 = FirebaseAuth.getInstance().getCurrentUser();

        final String[] user2 = {""};
        final byte[][] user1Secret = new byte[1][1];
        final byte[][] user2Secret = new byte[1][1];

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    user2[0] = chat.getReceiver();

             /*       // if there are 2 different participants in the chat
                    if (chat.getReceiver(). != chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(user1)) {*/


                        ECHDUser User1 = new ECHDUser(user1.getUid(), keyPairGen);
//                        System.out.println(User1.getName() + "’s public key is: " + User1.getPubKey().toString());

                        ECHDUser User2 = new ECHDUser(user2[0], keyPairGen);

                    try {
                        user1Secret[0] = User1.sendValueTo(User2);
                        user2Secret[0] = User2.sendValueTo(User1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!java.util.Arrays.equals(user1Secret[0], user2Secret[0]))
                    {
                        try {
                            throw new Exception("Shared secrets differ");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("Shared secrets are the same");

//                    }


                }
            }

            @Override
            public void onCancelled (@NonNull DatabaseError error){
            }

        });

       /* ECHDUser charlie = new ECHDUser("Alice", keyPairGen);
        System.out.println(charlie.getName() + "’s public key is: " + charlie.getPubKey().toString());*/

        ECHDUser denis = new ECHDUser("Bob", keyPairGen);
//        System.out.println(denis.getName() + "’s public key is: " + denis.getPubKey().toString());
        // The users exchange a private key using ECDH protocol. The values that they each receive will be printed.

        /*byte[] user1Secret;
        byte[] user2Secret;

        user1Secret = charlie.sendValueTo(denis);
        user2Secret = denis.sendValueTo(charlie);*/

        // prints differents bytes [B@d60ea24
//        System.out.println("Just Secrets: " + user1Secret + user2Secret);

 /*       if (!java.util.Arrays.equals(user1Secret, user2Secret))
        {
            throw new Exception("Shared secrets differ");
        }
        System.out.println("Shared secrets are the same");*/

        // la fel cu cea din ECHDUser
//        System.out.println("Secrets to HEX: " + toHexString(user1Secret) + "+" + toHexString(user2Secret));

        return user1Secret[0];

    }

}

