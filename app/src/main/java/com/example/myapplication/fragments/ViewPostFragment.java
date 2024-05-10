package com.example.myapplication.fragments;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static androidx.camera.core.impl.utils.ContextUtil.getApplicationContext;
import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Intent;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.BottomSheetDialog.BottomSheetReaction;
import com.example.myapplication.BottomSheetDialog.BottomSheetSetting;
import com.example.myapplication.Gesture.SimpleGestureFilter;
import com.example.myapplication.R;
import com.example.myapplication.activities.ChatActivity;
import com.example.myapplication.activities.MainActivity;
import com.example.myapplication.activities.RecentChatActivity;
import com.example.myapplication.activities.SearchUserActivity;
import com.example.myapplication.adapter.AddFriendAdapter;
import com.example.myapplication.adapter.FriendAdapter;
import com.example.myapplication.adapter.RecentChatRecyclerAdapter;
import com.example.myapplication.interfaces.AddFriend;
import com.example.myapplication.models.Chatroom;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import com.example.myapplication.activities.OnBackToCameraFragmentListener;
import com.example.myapplication.adapter.ViewPostAdapter;
import com.example.myapplication.models.NestedScrollableHost;
import com.example.myapplication.models.Post;
import com.example.myapplication.utils.AndroidUtils;
import com.example.myapplication.utils.FirebaseUtils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewPostFragment extends Fragment implements AddFriend {
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
        btnalluser.setText(user.getUsername());
        popupWindow.dismiss();

    }

    Button btnalluser;
    RelativeLayout layout;
    ImageButton ReactionBtn,home;
    RecyclerView rcvlistfriend;
    RecentChatRecyclerAdapter adapter;

    private SimpleGestureFilter detector;
    private FriendAdapter friendAdapter;
    PopupWindow popupWindow;
    View popUpView;

    private List<Post> posts;
    private ViewPostAdapter adapter;
    ViewPager2 viewPager2;
    ImageView btnBackToCamera;
    private OnBackToCameraFragmentListener mlistener;

    public ViewPostFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnBackToCameraFragmentListener) {
            mlistener = (OnBackToCameraFragmentListener) context;
        } else {
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

        adapter = new ViewPostAdapter(posts);
        viewPager2.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnalluser = view.findViewById(R.id.btnalluser);
        layout = view.findViewById(R.id.layout);
        ReactionBtn = view.findViewById(R.id.btn_Reaction);
        home = view.findViewById(R.id.home);

        btnalluser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                popUpView = inflater.inflate(R.layout.activity_allfriend, null);

                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                boolean focusable = true;
                popupWindow = new PopupWindow(popUpView, width, height, focusable);
//                popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
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
                            }
                            friendAdapter.addItem(new User("Tất cả mọi người"));
                        }
                    }
                });
                friendAdapter = new FriendAdapter(true, ViewPostFragment.this);
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
        nestedScrollableHost.setViewPager2(viewPager2);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {

                    nestedScrollableHost.setScrollable(true);
                } else {
                    nestedScrollableHost.setScrollable(false);
                }
            }
        });
        btnBackToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mlistener != null) {
                    viewPager2.setCurrentItem(0);
                    mlistener.onBackToCameraFragment();

                }
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetReaction bottomSheetReaction = new BottomSheetReaction();
                bottomSheetReaction.show(getChildFragmentManager(),"TAG");
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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference postsRef = db.collection("posts");
        postsRef.whereIn("userId", friends)
                .whereIn("visibility", Arrays.asList("public", "private"))
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        // Xử lý lỗi nếu có
                        return;
                    }
                    posts.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Post post = document.toObject(Post.class);
//                        if (document.getString("visibility").equals("public") ||
//                                (document.getString("visibility").equals("private") &&
//                                        document.contains("allowed_users") &&
//                                        document.get("allowed_users", Arrays.class).contains(FirebaseUtils.currentUserID()))) {
//                            posts.add(post);
//                        }
                        if(post.getVisibility().equals("public")||
                           post.getVisibility().equals("private")&&post.getAllowed_users().contains(FirebaseUtils.currentUserID())){
                            posts.add(post);
                        }
                    }
                    AndroidUtils.showToast(getContext(), "Check " + queryDocumentSnapshots.size());
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
        args.putIntArray("buttonLocation", new int[]{buttonLocation[0], dialogY});

        ReactionDialog reactionDialog = new ReactionDialog();
        reactionDialog.setArguments(args);
        reactionDialog.show(getChildFragmentManager(), "ReactionDialog");
    }

}
