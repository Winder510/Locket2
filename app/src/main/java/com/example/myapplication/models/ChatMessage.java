package com.example.myapplication.models;

import com.google.firebase.Timestamp;

public class ChatMessage {
    private String message;
    private String senderId;
    private Timestamp timestamp;
    private String imageUrl;
    private Timestamp imageDate;
    private String imageCaption;

    public ChatMessage() {
    }

    public ChatMessage(String message, String senderId, Timestamp timestamp,String imageUrl) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.imageUrl= imageUrl;
    }

    public ChatMessage(String message, String senderId, Timestamp timestamp,String imageUrl,Timestamp imageDate,String imageCaption) {
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.imageUrl= imageUrl;
        this.imageDate=imageDate;
        this.imageCaption=imageCaption;
    }

    public Timestamp getImageDate() {
        return imageDate;
    }

    public void setImageDate(Timestamp imageDate) {
        this.imageDate = imageDate;
    }

    public String getImageCaption() {
        return imageCaption;
    }

    public void setImageCaption(String imageCaption) {
        this.imageCaption = imageCaption;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
