package com.example.myapplication.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activities.RecentChatActivity;
import com.example.myapplication.activities.SettingsActivity;
import com.example.myapplication.adapter.AllPostAdapter;
import com.example.myapplication.adapter.FriendAdapter;
import com.example.myapplication.interfaces.AddFriend;
import com.example.myapplication.interfaces.OnDataPassListener;
import com.example.myapplication.interfaces.OnItemClickListener;
import com.example.myapplication.models.Post;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.AndroidUtils;
import com.example.myapplication.utils.FirebaseUtils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import maes.tech.intentanim.CustomIntent;

public class AllPostFragment extends Fragment implements AddFriend {

    RecyclerView recyclerView;
    ArrayList<Post> posts;
    AllPostAdapter adapter;
    View popUpView;
    PopupWindow popupWindow;
    RecyclerView rcvlistfriend;
    private OnDataPassListener onDataPassListener;
    private FriendAdapter friendAdapter;
    Button btnalluser;
    ImageButton btnSetting,btnRecentChat;
    User currentUserFilter;
    User currentUser;

    public void setOnDataPassListener(OnDataPassListener listener) {
        this.onDataPassListener = listener;
    }
    public AllPostFragment() {

    }
    public static AllPostFragment newInstance() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        posts = new ArrayList<>();
        getBungle();
    }
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_post, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        btnalluser = rootView.findViewById(R.id.btnalluser);
        btnSetting = rootView.findViewById(R.id.btnSetting);
        btnRecentChat = rootView.findViewById(R.id.btnRecentChat);
        FirebaseUtils.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    AndroidUtils.setProfilePic(requireContext(),uri,btnSetting);
                });
        adapter = new AllPostAdapter(posts, getContext(), new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                onDataPassListener.onDataPass(position, true);
                getParentFragmentManager().popBackStack();

            }
        });

        int numberOfColumns = 3;
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        if(currentUserFilter!=null){
            btnalluser.setText(currentUserFilter.getUsername());
        }
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int parentWidth = recyclerView.getWidth();
                int itemWidth = parentWidth / numberOfColumns ;
                int itemHeight = itemWidth; // Chiều cao bằng chiều rộng để đảm bảo tỷ lệ 1:1

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                for (int i = 0; i < layoutManager.getChildCount(); i++) {
                    View view = layoutManager.getChildAt(i);
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
                    params.width = itemWidth-10;
                    params.height = itemHeight-10;
                    params.setMargins(10, 10, 10, 10);
                    view.setLayoutParams(params);
                }
            }
        });
       return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnalluser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                popUpView = inflater.inflate(R.layout.activity_allfriend, null);
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                boolean focusable = true;
                popupWindow = new PopupWindow(popUpView, width, height, focusable);
                popupWindow.showAsDropDown(btnalluser);
                rcvlistfriend = popUpView.findViewById(R.id.listfriend);

                FirebaseUtils.currentUserDetail().get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User user = task.getResult().toObject(User.class);
                        if (user != null && user.getFriends() != null) {
                            for (int i = 0; i < user.getFriends().size(); i++) {
                                FirebaseUtils.allUserCollectionReference().document(user.getFriends().get(i)).get().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        User friend = task1.getResult().toObject(User.class);
                                        if (friend != null) {
                                            friendAdapter.addItem(friend);
                                        }
                                    }
                                });
                                if (i == user.getFriends().size() - 1) {
                                    friendAdapter.addItem(new User("Mọi người"));
                                    friendAdapter.addItem(new User("Bạn"));
                                }
                            }
                        }
                    }
                });
                friendAdapter = new FriendAdapter(true, AllPostFragment.this,getContext());
                rcvlistfriend.setAdapter(friendAdapter);
                popUpView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });

            }
        });
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClickSettingButton();
                Toast.makeText(requireActivity(), "Settings", Toast.LENGTH_SHORT).show();
            }
        });
        btnRecentChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), RecentChatActivity.class);
                startActivity(intent);
                CustomIntent.customType(requireContext(), "left-to-right");
            }
        });
    }
    private void handleClickSettingButton() {

        Intent intent = new Intent(requireContext(), SettingsActivity.class);
        FirebaseUtils.currentUserDetail().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                currentUser = task.getResult().toObject(User.class);
                if (currentUser != null) {
                    AndroidUtils.passUserModelAsIntent(intent, currentUser);
                    startActivity(intent);
                    CustomIntent.customType(requireContext(), "right-to-left");

                }

            }
        });
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

    @Override
    public void onAddFriend(String userId, String username, int position) {

    }

    @Override
    public void onClick(User user) {
        btnalluser.setText(user.getUsername());
        onDataPassListener.onFnUserFilterPass(user);
        popupWindow.dismiss();
        if (user.getUsername().equals("Mọi người")) {
            loadPosts();
        } else if (user.getUsername().equals("Bạn")) {
            handlefilterPost(FirebaseUtils.currentUserID());
        } else {
            handlefilterPost(user.getUserId());
        }
    }

    @Override
    public void unFriend(String userId, int position) {

    }

    private void handlefilterPost(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference postsRef = db.collection("posts");
        postsRef.whereIn("userId", Collections.singletonList(userId))
                .whereIn("visibility", Arrays.asList("public", "private"))
                .orderBy("created_at", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        return;
                    }
                    posts.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Post post = document.toObject(Post.class);
                        if (post.getVisibility().equals("public") || (post.getVisibility().equals("private") && post.getUserId().equals(FirebaseUtils.currentUserID())) ||
                                (post.getVisibility().equals("private") && post.getAllowed_users().contains(FirebaseUtils.currentUserID()))) {
                            posts.add(post);
                        }
                    }
                    AndroidUtils.showToast(getContext(), "Check " + queryDocumentSnapshots.size());
                    adapter.notifyDataSetChanged();
                });
    }
    void getBungle(){
        Bundle bundle = getArguments();
        if (bundle != null) {
            currentUserFilter = new User((User) Objects.requireNonNull(bundle.getSerializable("currentUserFilter")));
            if (currentUserFilter.getUsername().equals("Mọi người")) {
                loadPosts();
            }
            else if(currentUserFilter.getUsername().equals("Bạn")){
                handlefilterPost(FirebaseUtils.currentUserID());
            }
            else{
                handlefilterPost(currentUserFilter.getUserId());
            }
        }
        else{
            loadPosts();
        }
    }
}