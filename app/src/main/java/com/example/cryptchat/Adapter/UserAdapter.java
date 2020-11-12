package com.example.cryptchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cryptchat.Activities.MessageActivity;
import com.example.cryptchat.Model.Users;
import com.example.cryptchat.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<Users> mUsers;
    private boolean isChat;


    // Constructors
    public UserAdapter(Context context, List<Users> mUsers, boolean isChat) {
        this.context = context;
        this.mUsers = mUsers;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Users users = mUsers.get(position);
        holder.username.setText(users.getUsername());

        if (users.getImageURL().equals("default")) {
            holder.imageView.setImageResource(R.drawable.user);
        }
        else {
            int thumbnailSize = 60; // 60 x 60 dp for profile picture in user_item
            Glide.with(context)
                    .load(users.getImageURL())
                    .thumbnail(
                            Glide.with(context)
                                    .load(users.getImageURL())
                                    .override(thumbnailSize, thumbnailSize))
                    .into(holder.imageView);
        }

        // Status check

        if (isChat) {

            // if the user is online, set the green status
            if (users.getStatus().equals("online")) {
                holder.imageStatusOnline.setVisibility(View.VISIBLE);
                holder.imageStatusOffline.setVisibility(View.GONE);
            }
            // if the user is offline, set the red status
            else {
                holder.imageStatusOffline.setVisibility(View.VISIBLE);
                holder.imageStatusOnline.setVisibility(View.GONE);
            }
        }
        else {
            holder.imageStatusOnline.setVisibility(View.GONE);
            holder.imageStatusOffline.setVisibility(View.GONE);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MessageActivity.class);
                i.putExtra("userid", users.getId());
                context.startActivity(i);
            }
        });



    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView imageView;
        public ImageView imageStatusOnline;
        public ImageView imageStatusOffline;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_textView);
            imageView = itemView.findViewById(R.id.user_imageView);
            imageStatusOnline = itemView.findViewById(R.id.user_status_online);
            imageStatusOffline = itemView.findViewById(R.id.user_status_offline);
        }

    }



}
