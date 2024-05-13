package com.example.myapplication.models;

public class UserReaction {
    private String uId;
    private String name;
    private int reactionType;

    public UserReaction(String profileImgID, String name, int reactionType) {
        this.uId = profileImgID;
        this.name = name;
        this.reactionType = reactionType;
    }
    public String getUid() {
        return uId;
    }

    public void setUID(String profileImgID) {
        this.uId = profileImgID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getReactionType() {
        return reactionType;
    }

    public void setReactionType(int reactionType) {
        this.reactionType = reactionType;
    }
}
