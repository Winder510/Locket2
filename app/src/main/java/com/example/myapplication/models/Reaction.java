package com.example.myapplication.models;

import com.google.firebase.Timestamp;

public class Reaction {
    private String userId;
    private String reactionType;
    private Timestamp timestamp;

    public Reaction() {
    }

    public Reaction(String userId, String reactionType, Timestamp timestamp) {
        this.userId = userId;
        this.reactionType = reactionType;
        this.timestamp = timestamp;
    }
    public String getReactionType() {
        return reactionType;
    }

    public void setReactionType(String reactionType) {
        this.reactionType = reactionType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
