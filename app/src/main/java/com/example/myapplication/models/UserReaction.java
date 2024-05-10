package com.example.myapplication.models;

public class UserReaction {
    private int profileImgID;
    private String name;
    private int reactionType;

    public UserReaction(int profileImgID, String name, int reactionType) {
        this.profileImgID = profileImgID;
        this.name = name;
        this.reactionType = reactionType;
    }
    public int getProfileImgID() {
        return profileImgID;
    }

    public void setProfileImgID(int profileImgID) {
        this.profileImgID = profileImgID;
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
