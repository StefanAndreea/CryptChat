package com.example.cryptchat.Activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cryptchat.Adapter.MediaAdapter;
import com.example.cryptchat.Adapter.MessageAdapter;
import com.example.cryptchat.Crypto.GeneratedCert;
import com.example.cryptchat.Crypto.VernamCipher;
import com.example.cryptchat.Model.Chat;
import com.example.cryptchat.Model.Users;
import com.example.cryptchat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.spongycastle.util.encoders.Hex;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MessageActivity extends AppCompatActivity {

    // flag ce are ciclu de viata 24h de la creere
    public static int flagSessionKey;

    public static boolean verif_sign;
    public static byte[] signature;


    // Variables for Sending an Image
    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    final ArrayList<String> mediaIdList = new ArrayList<>();


    // Instantiate Widgets
    TextView username;
    ImageView imageView;
    EditText msg_editText;
    ImageButton sendBtn, backButton, imageButton;

    // Firebase Instantiations
    FirebaseUser fUser;
    DatabaseReference reference;
    Intent intent;

    private RecyclerView.LayoutManager mediaLayoutManager;
    RecyclerView textRecyclerView, mediaRecyclerView;
    MessageAdapter messageAdapter;
    MediaAdapter mediaAdapter;
    List<Chat> mChat;
    ArrayList<String> mMediaList;
    boolean hideShowMediaBtn;
    String userid;

    ValueEventListener seenListener;

    String chatChildID = "";

    // Encryption tools instantations
    VernamCipher vernam = new VernamCipher();
    public String VernamKey = "";


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("~!~!~ FLAG VALUE la incep onCreate : " + flagSessionKey);
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Makes the window fullscreen
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_message);

        // Widgets
        backButton = findViewById(R.id.back_btn);
        imageView = findViewById(R.id.imageview_profile);
        username = findViewById(R.id.user_name);
        msg_editText = findViewById(R.id.text_send);
        sendBtn = findViewById(R.id.btn_send);
        imageButton = findViewById(R.id.btn_image);

        // Text RecyclerView
        textRecyclerView = findViewById(R.id.recycler_View);
        textRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        textRecyclerView.setLayoutManager(linearLayoutManager);

        // Encryption Vernam Key
        VernamKey = vernam.shareKeyAcrossUsers();
        System.out.println("\n VERNAM KEY: " + VernamKey);
        vernam.setKey(VernamKey);
        Chat chat = new Chat();
        chat.setSessionKey(VernamKey);

        System.out.println("\n Set VERNAM: " + VernamKey);

        intent = getIntent();
        userid = intent.getStringExtra("userid");

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("MyUsers").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // am comentat -> cod nou
                //recyclerView.setAdapter(messageAdapter);

                Users user = dataSnapshot.getValue(Users.class);
                username.setText(user.getUsername());

                if (user.getImageURL().equals("default")) {
                    imageView.setImageResource(R.drawable.user);
                } else {
                    int thumbnailSize = 60; // 60 x 60 dp for profile picture in user_item
                    // old code: Glide.with(MessageActivity.this)
                    Glide.with(getApplicationContext())
                            .load(user.getImageURL())
                            .thumbnail(
                                    Glide.with(getApplicationContext())
                                            .load(user.getImageURL())
                                            .override(thumbnailSize, thumbnailSize))
                            .into(imageView);
                }
                initializeMedia();
                readMessage(fUser.getUid(), userid, user.getImageURL());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v){
                System.out.println("~!~!~ FLAG VALUE imed dupa onClick : " + flagSessionKey);

                /*message = vernam.encryption(message, VernamKey, cipherText);
                System.out.println("cipherText: " + cipherText);
                System.out.println("MSG: " + message);*/

                String msg = msg_editText.getText().toString();

                System.out.println("\nMSG before: " + msg);
                String cipherText = "";

                try {
                    GeneratedCert.init("RSA", GeneratedCert.KeySize.BIT_2048, GeneratedCert.CENTURY, true);
                    GeneratedCert.SignatureAlgorithm signatureAlgorithm = GeneratedCert.SignatureAlgorithm.SHA1WithRSA;
                    signature = GeneratedCert.sign(msg, "RSA", signatureAlgorithm);
                    Log.d("RSA Test", "x: " + Hex.toHexString(signature));
                }catch (Exception e)
                    {
                        Log.d("RSA Test", "ERROR: " + e.toString());
                    }

                    System.out.println("\n VERNAM Encryption: " + VernamKey);

                msg = vernam.encryption(msg, VernamKey, cipherText);

                System.out.println("\nMSG after: " + msg);

                /* CIFRAREA VERNAM TREBUIE SA FIE INAINTE DE A SE TRIMITE PE SERVER,
                DAR DUPA PRELUAREA TEXTULUI DE LA UTILIZATOR */

                if (msg.equals("") || msg.equals(" ") || msg.equals("\n")) {
                    Toast.makeText(MessageActivity.this, "You can't send an empty message!", Toast.LENGTH_SHORT).show();
                } else {
                    if (flagSessionKey == 0) // prima data in 24h => se poate genera cheia de sesiune
                    {
                        System.out.println("~!~!~ FLAG VALUE la verif dc == 0 : " + flagSessionKey);
                        sendMessage(fUser.getUid(), userid, msg, VernamKey);
                        cooldownSessionKey();
                        System.out.println("~!~!~ FLAG VALUE dupa functia cooldown : " + flagSessionKey);

                        Toast.makeText(MessageActivity.this, "A secure session for 24 hours has been created!", Toast.LENGTH_SHORT).show();
                        // Blocam butonul pentru ca am creat cheia de sesiune
                        flagSessionKey = 1;
                        System.out.println("~!~!~ FLAG VALUE : " + flagSessionKey);

                    } else if (flagSessionKey == 1) // userul este in cursul celor 24h => buton blocat
                    {
                        System.out.println("~!~!~ FLAG VALUE la verif dc == 1 : " + flagSessionKey);
                        sendMessage(fUser.getUid(), userid, msg, VernamKey);

                    }
                    System.out.println("~!~!~ FLAG VALUE in afara IF-ului : " + flagSessionKey);
                    // Create a new secure session -> generate new kew session every time a new convo starts


                }
                msg_editText.setText("");
            }
        });


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        seenMessage(userid);

    }

    // Method to add the functionality to let an User Select an Image from the Gallery
    private void SelectImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select an image to send"),  IMAGE_REQUEST);
    }

    // Checking the extension of the file (supposed to be an image)
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        // Going from the Gallery back to the app and telling it that the user has selected an image
        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_REQUEST){
                if(data.getClipData() == null){
                    mediaUriList.add(data.getData().toString());
                }else{
                    for(int i = 0; i < data.getClipData().getItemCount(); i++){
                        mediaUriList.add(data.getClipData().getItemAt(i).getUri().toString());
                    }
                }
                mediaAdapter.notifyDataSetChanged();
            }
        }
    }


    // Media Recycler View
    ArrayList<String> mediaUriList = new ArrayList<>();
    private void initializeMedia() {
        mediaUriList = new ArrayList<>();
        mediaRecyclerView = findViewById(R.id.photo_view);
        mediaRecyclerView.setNestedScrollingEnabled(false);
        mediaRecyclerView.setHasFixedSize(true);
        mediaLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false);
        mediaRecyclerView.setLayoutManager(mediaLayoutManager);
        mediaAdapter = new MediaAdapter(getApplicationContext(), mediaUriList);
        mediaRecyclerView.setAdapter(mediaAdapter);
    }


    // Method to implement the Sending a Message on the Server Functionality
    private void sendMessage(String sender, String receiver, String message, String VernamKey) {

        final DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference().child("Chats");
        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isSeen", false);
        hashMap.put("sessionKey", VernamKey);
        hashMap.put("hasMedia", false);

        chatChildID = chatReference.push().getKey();
        System.out.println("\nCHAT CHILD ID: " + chatChildID);
        chatReference.child(chatChildID).setValue(hashMap);

        // another hash with the media
//      hashMap.put("media", uri.toString());

        if(!mediaUriList.isEmpty()) {
            for (String mediaUri : mediaUriList) {

                final String mediaID = chatReference.child(chatChildID).child("mediaUri").push().getKey();
                mediaIdList.add(mediaID);
                final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Chats").child(fUser.getUid()).child(mediaID);
                UploadTask uploadTask = filepath.putFile(Uri.parse(mediaUri));

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                int totalMediaUploaded = 0;
                                hashMap.put("/mediaUri", uri.toString());
                                hashMap.put("hasMedia", true);

                                totalMediaUploaded++;
                                if(totalMediaUploaded == mediaUriList.size())
                                {
                                    ChatHasMedia(uri.toString());
                                    updateDatabase(chatReference.child(chatChildID), hashMap);
                                }

                            }
                        });
                    }
                });

            }
        }

        else {
            if(!message.isEmpty())
                updateDatabase(chatReference.child(chatChildID), hashMap);
        }

        // Adding User to chat fragment: recent chats with contacts
        final DatabaseReference chatRef = FirebaseDatabase.getInstance()
                .getReference("ChatList")
                .child(fUser.getUid()) // logged in user
                .child(userid); // the user who is talking with

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // if the chat doesn't exist in the Chat Fragment, we add it searching by the userid
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userid);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }


    private void updateDatabase(DatabaseReference newMessageDB, HashMap newMessageMap) {
        newMessageDB.updateChildren(newMessageMap);
        mediaUriList.clear();
        mediaIdList.clear();
        mediaAdapter.notifyDataSetChanged();
    }

    private void readMessage(final String myid, final String userid, final String imageurl) {
        mChat = new ArrayList<>();
        mMediaList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                mMediaList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    // if there are 2 different participants in the chat
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {

                        VernamKey = chat.getSessionKey();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("sessionKey", VernamKey);
                        snapshot.getRef().updateChildren(hashMap);

                        System.out.println("\n VERNAM From Server: " + VernamKey);

                        String newTextMessage = "";
                        String encrpytedMessage = chat.getMessage();

                        System.out.println("\n Encrypted Message from the server:" + encrpytedMessage);

                        System.out.println("\n VERNAM Decryption: " + VernamKey);

                        newTextMessage = newTextMessage + vernam.decryption(encrpytedMessage, VernamKey, newTextMessage);
                        System.out.println("\nNEW TEST MESSAGE: " + newTextMessage);

                        GeneratedCert.SignatureAlgorithm signatureAlgorithm = GeneratedCert.SignatureAlgorithm.SHA1WithRSA;

                        try {
//                            byte[] x = GeneratedCert.sign(newTextMessage, "RSA", signatureAlgorithm);
//                            Log.d("RSA Test", "x: " + Hex.toHexString(x));
                            signature = GeneratedCert.sign(newTextMessage, "RSA", signatureAlgorithm);
                            verif_sign = GeneratedCert.verify(newTextMessage, signature, "RSA", signatureAlgorithm);
                        } catch (KeyStoreException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (CertificateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (UnrecoverableEntryException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        } catch (SignatureException e) {
                            e.printStackTrace();
                        }
                        Log.d("RSA Test", "y: " + (verif_sign ? "True" : "False"));

                        chat.setMessage(newTextMessage);

                        // Getting the media
                        if (dataSnapshot.child("mediaUri").exists()) {
                            for (DataSnapshot mediaSnapshot : dataSnapshot.child("mediaUri").getChildren()) {
                                mMediaList.add(mediaSnapshot.getValue().toString());
                                chat.setMediaUri(mediaSnapshot.getValue().toString());
                                chat.setHasMedia(true);
                            }
                        }

                        mChat.add(chat);

                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imageurl);
                    textRecyclerView.setAdapter(messageAdapter);

                    /*mediaAdapter = new MediaAdapter(MessageActivity.this, mMediaList);
                    mediaRecyclerView.setAdapter(mediaAdapter);*/

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void seenMessage(final String userid) {

        reference = FirebaseDatabase.getInstance().getReference("Chats");

        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);

                        // basically if the Receiver and the Sender are in the same convo at the same time
                        if (chat.getReceiver().equals(fUser.getUid()) && chat.getSender().equals(userid)) {

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("isSeen", true);

                            snapshot.getRef().updateChildren(hashMap);
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void CheckStatus(String status) {
        reference = FirebaseDatabase.getInstance().getReference("MyUsers").child(fUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }


    private void ChatHasMedia(final String mediaURL) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats").child(chatChildID);

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("hasMedia", true);
            hashMap.put("mediaUri", mediaURL);
            reference.updateChildren(hashMap);

    }



    int createSharedSessionKey() {
        Random rand = new Random();
        return rand.nextInt(100);
    }

    void cooldownSessionKey() {
        System.out.println("~!~!~ FLAG VALUE la incep fct cooldown : " + flagSessionKey);
        final LinkedList<Integer> sessionKeys = new LinkedList<>();
        sessionKeys.add(createSharedSessionKey());

        // 24 hours = 86400000 miliseconds
        new CountDownTimer(30000, 10000) {
            public void onTick(long millisUntilFinished) {
                System.out.println("List of keys before " + sessionKeys);
            }

            public void onFinish() {
                System.out.println("~!~!~ FLAG VALUE la incep onFinish : " + flagSessionKey);
                sessionKeys.pop(); // s-au dus cele 24h, se scoate cheia
                flagSessionKey = 0; // butonul de send poate crea chei noi
                System.out.println("List of keys after popping " + sessionKeys);
                System.out.println("~!~!~ FLAG VALUE la finalul onFinish : " + flagSessionKey);

            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // If the user quits the app, the messages are not gonna be Seen
        reference.removeEventListener(seenListener);
        // and the status of the user will be Offline
//        CheckStatus("offline");
    }


}
