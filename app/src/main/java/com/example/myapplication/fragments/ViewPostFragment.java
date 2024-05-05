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
import com.example.myapplication.activities.RecentChatActivity;
import com.example.myapplication.activities.SearchUserActivity;
import com.example.myapplication.adapter.RecentChatRecyclerAdapter;
import com.example.myapplication.models.Chatroom;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewPostFragment extends Fragment {
    Button btnalluser,btnall;
    RelativeLayout layout;

    RecyclerView recyclerView;
    RecentChatRecyclerAdapter adapter;

    private SimpleGestureFilter detector;



    // TODO: Rename and change types and number of parameters
    public static ViewPostFragment newInstance(String param1, String param2) {
        ViewPostFragment fragment = new ViewPostFragment();
        return fragment;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnalluser=view.findViewById(R.id.btnalluser);
        layout=view.findViewById(R.id.layout);

        btnalluser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View popUpView =inflater.inflate(R.layout.activity_allfriend,null);

                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height=ViewGroup.LayoutParams.MATCH_PARENT;
                boolean focusable=true;
                PopupWindow popupWindow= new PopupWindow(popUpView,width,height,focusable);
                popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);

                recyclerView=popUpView.findViewById(R.id.recyler_view_allfriend);
                btnall=popUpView.findViewById(R.id.btnall);

                Query query = FirebaseUtils.allChatroomCollectionReference()
                        .whereArrayContains("userIds", FirebaseUtils.currentUserID())
                        .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING);

                FirestoreRecyclerOptions<Chatroom> options = new FirestoreRecyclerOptions.Builder<Chatroom>().setQuery(query, Chatroom.class).build();
                adapter = new RecentChatRecyclerAdapter(options, getContext().getApplicationContext());
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                recyclerView.setAdapter(adapter);
                adapter.startListening();

                btnall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i= new Intent(requireContext(),RecentChatActivity.class);
                        startActivity(i);
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
}