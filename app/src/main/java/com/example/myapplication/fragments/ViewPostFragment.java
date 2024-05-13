package com.example.myapplication.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.BottomSheetDialog.BottomSheetReaction;
import com.example.myapplication.Gesture.SimpleGestureFilter;
import com.example.myapplication.R;
import com.example.myapplication.activities.SettingsActivity;
import com.example.myapplication.adapter.FriendAdapter;
import com.example.myapplication.interfaces.AddFriend;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.FirebaseUtils;

import android.view.MotionEvent;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import com.example.myapplication.interfaces.OnBackToCameraFragmentListener;
import com.example.myapplication.adapter.AllPostAdapter;
import com.example.myapplication.adapter.ViewPostAdapter;
import com.example.myapplication.interfaces.AddFriend;
import com.example.myapplication.interfaces.OnBackToCameraFragmentListener;
import com.example.myapplication.interfaces.OnDataPassListener;
import com.example.myapplication.models.NestedScrollableHost;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import maes.tech.intentanim.CustomIntent;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewPostFragment extends Fragment implements AddFriend, OnDataPassListener {

    Button btnalluser,btnActive;
    RelativeLayout layout;
    ImageButton ReactionBtn,allPost,optionPost;
    RecyclerView rcvlistfriend;
    ViewPostAdapter adapter;
    TextView noPostTextView;
    LinearLayout action1,topLayout;
    private SimpleGestureFilter detector;
    private FriendAdapter friendAdapter;
    PopupWindow popupWindow;
    View popUpView;
    EditText sendmes;
    private List<Post> posts;
    ViewPager2 viewPager2;
    ImageView btnBackToCamera;
    User currentUser;
    ImageView profile_pic_image_view;
    User currentUserFilter;


    private OnBackToCameraFragmentListener mlistener;

    public ViewPostFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnBackToCameraFragmentListener) {
            mlistener = (OnBackToCameraFragmentListener) context;
        }else {
            throw new RuntimeException(context.toString());
        }
    }

    public static ViewPostFragment newInstance() {
        return new ViewPostFragment();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_post, container, false);
        viewPager2 = rootView.findViewById(R.id.viewpager2);
        adapter = new ViewPostAdapter(posts, getContext());
        viewPager2.setAdapter(adapter);
        FirebaseUtils.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    AndroidUtils.setProfilePic(requireContext(),uri,btnSetting);
                });
        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        posts = new ArrayList<>();
        loadPosts();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NestedScrollableHost nestedScrollableHost = view.findViewById(R.id.nestedScrollableHost);
        btnBackToCamera = view.findViewById(R.id.btnBackToCamera);
        btnalluser = view.findViewById(R.id.btnalluser);
        layout = view.findViewById(R.id.layout);
        ReactionBtn = view.findViewById(R.id.btn_Reaction);
        btnActive= view.findViewById(R.id.btnActive);
        sendmes=view.findViewById(R.id.sendmes);
        btnSetting = view.findViewById(R.id.btnSetting);
        profile_pic_image_view = view.findViewById(R.id.profile_pic_image_view);

        noPostTextView = view.findViewById(R.id.noPostTextView);
        allPost = view.findViewById(R.id.allPost);
        optionPost = view.findViewById(R.id.optionPost);
        action1 =view.findViewById(R.id.action1Layout);
        topLayout =view.findViewById(R.id.topLayout);

        nestedScrollableHost.setViewPager2(viewPager2);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(posts.isEmpty()){
                    noPostTextView.setVisibility(View.VISIBLE);
                    sendmes.setVisibility(View.GONE);
                    ReactionBtn.setVisibility(View.GONE);
                }
                else{
                    noPostTextView.setVisibility(View.GONE);
                }
                if (position == 0) {
                    nestedScrollableHost.setScrollable(true);
                } else {
                    nestedScrollableHost.setScrollable(false);
                }
                if(!posts.isEmpty()&&posts.get(viewPager2.getCurrentItem()).getUserId().equals(FirebaseUtils.currentUserID())){
                    btnActive.setVisibility(View.VISIBLE);
                    ReactionBtn.setVisibility(View.GONE);
                    sendmes.setVisibility(View.GONE);
                }else{
                    btnActive.setVisibility(View.GONE);
                    ReactionBtn.setVisibility(View.VISIBLE);
                    sendmes.setVisibility(View.VISIBLE);
                }
            }
        });
        allPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchF();

            }
        });
        btnBackToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mlistener != null) {
                    viewPager2.setCurrentItem(0,false);
                    mlistener.onBackToCameraFragment();
                }
            }
        });
        btnActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetReaction bottomSheetReaction = new BottomSheetReaction(posts.get(viewPager2.getCurrentItem()).getPostId());
                bottomSheetReaction.show(getChildFragmentManager(),"TAG");
            }
        });

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
                                    friendAdapter.addItem(new User("Tất cả mọi người"));
                                    friendAdapter.addItem(new User("Bạn"));
                                }
                            }
                            friendAdapter.addItem(new User("Mọi người"));

                        }
                    }
                });
                friendAdapter = new FriendAdapter(true, ViewPostFragment.this,getContext());
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
        ReactionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReactionDialog();
            }
        });
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClickSettingButton();
                Toast.makeText(requireActivity(), "Settings", Toast.LENGTH_SHORT).show();
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
    private void switchF(){
        AllPostFragment fragmentUpload = new AllPostFragment();
        Bundle bundle;
        if(currentUserFilter!=null){
            bundle = new Bundle();
            bundle.putSerializable("currentUserFilter", currentUserFilter);
            fragmentUpload.setArguments(bundle);
        }
        fragmentUpload.setOnDataPassListener(this);
        handleSetVisibleSomeLayout(false);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.viewAllPost, fragmentUpload)
                .addToBackStack(null)
                .commit();
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
                    adapter.notifyDataSetChanged();

                });
    }
    private void showReactionDialog() {
        // Lấy tọa độ của nút gọi và chiều cao của nút
        int[] buttonLocation = new int[2];
        ReactionBtn.getLocationOnScreen(buttonLocation);
        int buttonHeight = ReactionBtn.getHeight();

        // Tính toán lại tọa độ y để hiển thị Dialog phía trên của nút gọi
        int dialogY = buttonLocation[1] - buttonHeight;

        // Truyền tọa độ của nút gọi và DialogY cho ReactionDialog
        Bundle args = new Bundle();
        args.putString("currentPostID",posts.get(viewPager2.getCurrentItem()).getPostId());
        args.putIntArray("buttonLocation", new int[]{buttonLocation[0], dialogY});

        ReactionDialog reactionDialog = new ReactionDialog();
        reactionDialog.setArguments(args);
        reactionDialog.show(getChildFragmentManager(), "ReactionDialog");
    }
    @Override
    public void onAddFriend(String userId, String username, int position) {
        // Implement logic khi thêm bạn bè
    }

    @Override
    public void unFriend(String userId, int position) {
        // Implement logic khi hủy kết bạn
    }

    @Override
    public void onClick(User user) {
        // Implement logic khi click vào user
        currentUserFilter = new User(user);
        btnalluser.setText(user.getUsername());
        popupWindow.dismiss();
        if (user.getUsername().equals("Tất cả mọi người")) {
            loadPosts();
        }
        else if(user.getUsername().equals("Bạn")){
            handlefilterPost(FirebaseUtils.currentUserID());
        }
        else{
            handlefilterPost(user.getUserId());
        }
    }

    private void handlefilterPost(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference postsRef = db.collection("posts");
        postsRef.whereIn("userId", Collections.singletonList(userId))
                .whereIn("visibility", Arrays.asList("public", "private"))
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


    @Override
    public void onDataPass(Integer data,boolean visible) {
        viewPager2.setCurrentItem(data,false);
        handleSetVisibleSomeLayout(visible);
    }

    @Override
    public void onFnUserFilterPass(User user) {
        btnalluser.setText(user.getUsername());
        if (user.getUsername().equals("Tất cả mọi người")) {
            loadPosts();
        }
        else if(user.getUsername().equals("Bạn")){
            handlefilterPost(FirebaseUtils.currentUserID());
        }
        else{
            handlefilterPost(user.getUserId());
        }
    }

    private void  handleSetVisibleSomeLayout(boolean isVisible){
        if(isVisible){
            action1.setVisibility(View.VISIBLE);
            allPost.setVisibility(View.VISIBLE);
            optionPost.setVisibility(View.VISIBLE);
            topLayout.setVisibility(View.VISIBLE);
        }
        else {
            action1.setVisibility(View.INVISIBLE);
            allPost.setVisibility(View.INVISIBLE);
            optionPost.setVisibility(View.INVISIBLE);
            topLayout.setVisibility(View.INVISIBLE);
        }
    }
}
