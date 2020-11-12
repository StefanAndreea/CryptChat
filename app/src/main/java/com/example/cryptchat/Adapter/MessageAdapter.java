package com.example.cryptchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cryptchat.Activities.PhotoActivity;
import com.example.cryptchat.Model.Chat;
import com.example.cryptchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context context;
    private List<Chat> mChat;
    private String imgURL;
    public static final int hideViewMediaBtn = 0;
    public static final int showViewMediaBtn = 1;
    public static int flagPhoto;

    // Firebase
    FirebaseUser fUser;

    // The chats should be on the right if we send the messages,
    // and on the left side for the other person
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    // Constructors
    public MessageAdapter(Context context, List<Chat> mChat, String imgURL) {
        this.context = context;
        this.mChat = mChat;
        this.imgURL = imgURL;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        // checking if the messages are ours or not so we know where to display the chat side
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);

			return new MessageAdapter.ViewHolder(view);
            // return new ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);

			return new MessageAdapter.ViewHolder(view);
            // return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, int position) {
        final Chat chat = mChat.get(position);

        // diplaying the message
        holder.show_message.setText(chat.getMessage());

        // displaying the profile picture
        if (imgURL.equals("default")) {
            holder.profile_image.setImageResource(R.drawable.user);
        } else {
            int thumbnailSize = 60; // 60 x 60 dp for profile picture in user_item
            Glide.with(context)
                    .load(imgURL)
                    .thumbnail(
                            Glide.with(context)
                                    .load(imgURL)
                                    .override(thumbnailSize, thumbnailSize))
                    .into(holder.profile_image);
        }

        // displaying the media sent
        holder.btn_view_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, PhotoActivity.class);
                context.startActivity(i);
            }
        });


        if (position == mChat.size() - 1) {

            if (showViewButton() == 1) {
                holder.btn_view_media.setVisibility(View.VISIBLE);

            } else if (showViewButton() == 0) {
                holder.btn_view_media.setVisibility(View.GONE);
            }

        }
        else
            holder.btn_view_media.setVisibility(View.GONE);



        // Check the index of the chat's last message
        if (position == mChat.size() - 1) {


            if (chat.getIsSeen()) {
                holder.seen_text_view.setText("Seen");
            }
            else {
                holder.seen_text_view.setText("Delivered");
            }
        }
        else { // if the message is not delivered nor seen, we hide the textView
            holder.seen_text_view.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {
        return mChat.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        // Initialising Widgets
        TextView show_message;
        ImageView profile_image;
        TextView seen_text_view;
        Button btn_view_media;
        ImageView itemView_media;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            seen_text_view = itemView.findViewById(R.id.text_seen_status);
            btn_view_media = itemView.findViewById(R.id.btn_view_media);
            itemView_media = itemView.findViewById(R.id.item_viewmedia);

        }
    }


    public int showViewButton() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);


//                    System.out.println("\nPATH" + dataSnapshot.getChildrenCount());

                    if(chat.isHasMedia())
                    {
                        flagPhoto = 1;
//                        btn_view_media.setVisibility(View.VISIBLE);
//                        btn_view_media.setVisibility(View.GONE);
                    }
                    else
                        flagPhoto = 0;
//                    System.out.println("\nMEDIA URL: " + chat.getMediaUrl().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        if (flagPhoto == 1) {
            return showViewMediaBtn;
        } else {
            return hideViewMediaBtn;
        }
    }



    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        // if we are the sender of the message by checking the UID,
        // then our chat should be on the right side of the chat
        if (mChat.get(position).getSender().equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }

    }
}
