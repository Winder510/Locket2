package com.example.myapplication.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.adapter.AllPostAdapter;
import com.example.myapplication.models.Post;
import com.example.myapplication.utils.AndroidUtils;
import com.example.myapplication.utils.FirebaseUtils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AllPostFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Post> posts;
    AllPostAdapter adapter;

    public AllPostFragment() {
        // Required empty public constructor
    }


    public static AllPostFragment newInstance() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        posts = new ArrayList<>();
        loadPosts();
    }
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_post, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        adapter = new AllPostAdapter(posts,getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
       return rootView;
    }
    private void loadPosts() {
        AtomicReference<ArrayList<String>> friendsList = new AtomicReference<>();
        FirebaseUtils.currentUserDetail().addSnapshotListener((documentSnapshot, error) -> {
            if (error != null) {
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists() && documentSnapshot.contains("friends")) {
                Object friendsObject = documentSnapshot.get("friends");
                if (friendsObject instanceof ArrayList) {
                    friendsList.set((ArrayList<String>) friendsObject);
                    getPostsOfFriends(friendsList);
                }
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void getPostsOfFriends(AtomicReference<ArrayList<String>> friendsList) {
        ArrayList<String> friends = friendsList.get();
        if (friends == null || friends.isEmpty()) {
            return;
        }
        friends.add(FirebaseUtils.currentUserID());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference postsRef = db.collection("posts");
        postsRef.whereIn("userId", friends)
                .whereIn("visibility", Arrays.asList("public", "private"))
                .orderBy("created_at", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        // Xử lý lỗi nếu có
                        return;
                    }
                    posts.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Post post = document.toObject(Post.class);
                        if(post.getVisibility().equals("public")||(post.getVisibility().equals("private")&&post.getUserId().equals(FirebaseUtils.currentUserID()))||
                                (post.getVisibility().equals("private")&&post.getAllowed_users().contains(FirebaseUtils.currentUserID()))){
                            posts.add(post);
                        }
                    }
                    AndroidUtils.showToast(getContext(), "Check " + queryDocumentSnapshots.size());
                    adapter.notifyDataSetChanged();

                });
    }

}