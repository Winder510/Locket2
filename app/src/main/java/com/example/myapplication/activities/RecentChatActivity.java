package com.example.myapplication.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Gesture.SimpleGestureFilter;
import com.example.myapplication.Gesture.SimpleGestureFilter.SimpleGestureListener;
import com.example.myapplication.R;
import com.example.myapplication.adapter.RecentChatRecyclerAdapter;
import com.example.myapplication.models.Chatroom;
import com.example.myapplication.utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import maes.tech.intentanim.CustomIntent;

public class RecentChatActivity extends AppCompatActivity implements SimpleGestureListener {
    RecyclerView recyclerView;
    RecentChatRecyclerAdapter adapter;
    private SimpleGestureFilter detector;
    ImageButton btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_chat);
        recyclerView = findViewById(R.id.recyler_view);
        btnBack = findViewById(R.id.back_btn);
        setupRecyclerView();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                CustomIntent.customType(RecentChatActivity.this, "right-to-left");
            }
        });
    }

    void setupRecyclerView() {
        Query query = FirebaseUtils.allChatroomCollectionReference()
                .whereArrayContains("userIds", FirebaseUtils.currentUserID())
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Chatroom> options = new FirestoreRecyclerOptions.Builder<Chatroom>().setQuery(query, Chatroom.class).build();
        adapter = new RecentChatRecyclerAdapter(options, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        detector = new SimpleGestureFilter(RecentChatActivity.this, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {

        switch (direction) {
            case SimpleGestureFilter.SWIPE_RIGHT:
                onBackPressed();
                CustomIntent.customType(RecentChatActivity.this, "right-to-left");
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
                break;
            case SimpleGestureFilter.SWIPE_UP:
                break;

        }
    }

}