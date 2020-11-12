package com.example.cryptchat.Model;

public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private boolean isSeen;
    private String sessionKey;
    private String media;
    private boolean hasMedia;


    // Constructors
    public Chat() {
    }

    public Chat(String sender, String receiver, String message, boolean isSeen, String sessionKey, String media, boolean hasMedia) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isSeen = isSeen;
        this.sessionKey = sessionKey;
        this.media = media;
        this.hasMedia = hasMedia;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getIsSeen() {
        return isSeen;
    }

    public void setSeen(boolean isSeen) {
        this.isSeen = isSeen;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getMediaUri() {
        return media;
    }

    public void setMediaUri(String media) {
        this.media = media;
    }

    public boolean isHasMedia() {
        return hasMedia;
    }

    public void setHasMedia(boolean hasMedia) {
        this.hasMedia = hasMedia;
    }
}
