package com.example.myapplication.fragments;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static androidx.camera.core.impl.utils.ContextUtil.getApplicationContext;
import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.content.Context;
import android.view.LayoutInflater;
import androidx.core.content.ContextCompat;

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

    Button btnalluser, btnall;
    RelativeLayout layout;
    ImageButton ReactionBtn;
    RecyclerView rcvlistfriend;
    RecentChatRecyclerAdapter adapter;

    private SimpleGestureFilter detector;
    private FriendAdapter friendAdapter;
    PopupWindow popupWindow;
    View popUpView;


    // TODO: Rename and change types and number of parameters
    public static ViewPostFragment newInstance(String param1, String param2) {
        ViewPostFragment fragment = new ViewPostFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnalluser = view.findViewById(R.id.btnalluser);
        layout = view.findViewById(R.id.layout);
        ReactionBtn = view.findViewById(R.id.btn_Reaction);

        btnalluser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                popUpView = inflater.inflate(R.layout.activity_allfriend, null);

                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                boolean focusable = true;
                popupWindow = new PopupWindow(popUpView, width, height, focusable);
                popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);

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
                        }
                    }
                });
                friendAdapter = new FriendAdapter(true, ViewPostFragment.this);
                rcvlistfriend.setAdapter(friendAdapter);
                btnall = popUpView.findViewById(R.id.btnall);
                btnall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnalluser.setText("Tất cả bạn bè");
                        popupWindow.dismiss();
                    }
                });


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
                Toast.makeText(requireContext(), "Show button", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<User> filterFriends(List<User> data) {
        ArrayList<User> newList = new ArrayList<>(data);
        for (int i = 0; i < data.size(); i++) {
            for (User user : friendAdapter.getList()) {
                if (data.get(i).getUserId().equals(user.getUserId())) {
                    newList.remove(data.get(i));
                    break;
                }
            }
        }
        return newList;
    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_post, container, false);
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