package com.example.myapplication.models;

import com.google.firebase.Timestamp;

import java.util.List;
import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String phone;
    private String username;
    private Timestamp createdTimestamp;
    private String birthday;
    private List<String> friends;
    private String userId;

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public User(User other)  {
        this.phone = other.phone;
        this.username = other.username;
        this.createdTimestamp = other.createdTimestamp;
        this.friends = other.friends;
        this.userId = other.userId;
        this.birthday = other.birthday;
    }
    public User() {
    }

    public User(String phone, String username, Timestamp createdTimestamp, List<String> friends, String userId,String birthday) {
        this.phone = phone;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.friends = friends;
        this.userId = userId;
        this.birthday = birthday;
    }

    public User(String username) {
            this.username = username;
        }

    public User(String phone, String username, Timestamp createdTimestamp,String userId) {
        this.phone = phone;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }
}
