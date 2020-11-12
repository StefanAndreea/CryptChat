package com.example.cryptchat.Model;

import java.security.PrivateKey;
import java.security.PublicKey;

public class Users {

    private String id;
    private String username;
    private String imageURL;
    private String status;

    private String publicKey;

    // Constructors
    public Users() {
    }

    public Users(String id, String username, String imageURL, String status, String publicKey) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.status = status;
        this.publicKey = publicKey;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

}
