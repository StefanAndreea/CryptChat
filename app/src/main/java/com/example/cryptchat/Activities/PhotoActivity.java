package com.example.cryptchat.Activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cryptchat.Model.Chat;
import com.example.cryptchat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PhotoActivity extends AppCompatActivity {

    ImageView image;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats");
    String imageURL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        image = findViewById(R.id.full_image);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    // - DE VERIFICAT DE CE SE AFISEAZA ULTIMA POZA INTOTDEAUNA
                    // Getting the media
                    if (dataSnapshot.child("mediaUri").exists()) {
//                        for (DataSnapshot mediaSnapshot : dataSnapshot.child("mediaUri").getChildren()) {
                            imageURL = chat.getMediaUri();
                            System.out.println("\nURI: " + chat.getMediaUri());

                            Glide.with(getApplicationContext())
                                    .load(imageURL)
                                    .thumbnail(
                                            Glide.with(getApplicationContext())
                                                    .load(imageURL))
                                    .override(500, 500)
                                    .into(image);
                        }
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
