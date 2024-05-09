package com.example.myapplication.models;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class Post {
    private String postCaption;
    private String postImg_url;
    private String userId;
    private String startCount;
    private String visibility;
    private ArrayList<String> allowed_users;
    private Timestamp created_at;

    public Post(){

    }
    public Post(String postCaption, String postImg_url, String userId, String startCount, String visibility, ArrayList<String> allowed_users, Timestamp created_at) {
        this.postCaption = postCaption;
        this.postImg_url = postImg_url;
        this.userId = userId;
        this.startCount = startCount;
        this.visibility = visibility;
        this.allowed_users = allowed_users;
        this.created_at = created_at;
    }

    public String getPostCaption() {
        return postCaption;
    }

    public void setPostCaption(String postCaption) {
        this.postCaption = postCaption;
    }

    public String getPostImg_url() {
        return postImg_url;
    }


    public void setPostImg_url(String postImg_url) {
        this.postImg_url = postImg_url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStartCount() {
        return startCount;
    }

    public void setStartCount(String startCount) {
        this.startCount = startCount;
    }


    public ArrayList<String> getAllowed_users() {
        return allowed_users;
    }

    public void setAllowed_users(ArrayList<String> allowed_users) {
        this.allowed_users = allowed_users;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
    public String getVisibility() {
        return visibility;
    }

}
