package com.example.myapplication.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.models.User;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FirebaseUtils {
    public static String currentUserID() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static boolean isLoggedIn() {
        if (currentUserID() != null) {
            return true;
        }
        return false;
    }

    public static DocumentReference currentUserDetail() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserID());
    }


    public static CollectionReference InviteReference() {
        return FirebaseFirestore.getInstance().collection("invites");
    }

    public static CollectionReference ChatReference() {
        return FirebaseFirestore.getInstance().collection("chats");
    }

    public static DocumentReference getUserInfor(String id){
        return FirebaseFirestore.getInstance().collection("users").document(id);
    }
    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static DocumentReference getChatroomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }
    public static DocumentReference getFriendDetail(String id) {
        return FirebaseFirestore.getInstance().collection("users").document(id);
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static String getChatroomId(String userId1, String userId2) {
        if (userId1.hashCode() < userId2.hashCode()) {
            return userId1 + "_" + userId2;
        }
        return userId2 + "_" + userId1;
    }

    public static CollectionReference allChatroomCollectionReference() {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static DocumentReference getOtherUserFromChatroom(List<String> userIds) {
        if (userIds.get(0).equals(FirebaseUtils.currentUserID())) {
            return allUserCollectionReference().document(userIds.get(1));
        } else {
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    public static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }

    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference getCurrentProfilePicStorageRef() {
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(FirebaseUtils.currentUserID());
    }
    public static StorageReference getCurrentPostImageStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("userPostImage")
                .child(FirebaseUtils.currentUserID());
    }
    public static StorageReference  getOtherProfilePicStorageRef(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(otherUserId);
    }
    public static CollectionReference getPostsCollectionReference(){
        return FirebaseFirestore.getInstance().collection("posts");
    }
    public static LiveData<List<User>> getFriendUsersLiveData(List<String> friendIds) {
        MutableLiveData<List<User>> friendUsersLiveData = new MutableLiveData<>();
        List<User> friendUsers = new ArrayList<>();

        for (String friendId : friendIds) {
            FirebaseUtils.getFriendDetail(friendId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    User friendUser = documentSnapshot.toObject(User.class);
                    if (friendUser != null) {
                        friendUsers.add(friendUser);
                    }
                }
                // Kiểm tra nếu đã lấy thông tin của tất cả bạn bè, sau đó cập nhật LiveData
                if (friendUsers.size() == friendIds.size()) {
                    friendUsersLiveData.setValue(friendUsers);
                }
            }).addOnFailureListener(e -> {
                // Xử lý lỗi nếu có
            });
        }

        return friendUsersLiveData;
    }

    public static CollectionReference getPostReactionReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("reactions");
    }
}
