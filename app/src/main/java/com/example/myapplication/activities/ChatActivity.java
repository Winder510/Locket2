package com.example.myapplication.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ChatRecyclerAdapter;
import com.example.myapplication.models.ChatMessage;
import com.example.myapplication.models.Chatroom;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.AndroidUtils;
import com.example.myapplication.utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {
    User otherUser;
    String chatroomId;
    Chatroom chatroom;
    ChatRecyclerAdapter adapter;
    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;
    ImageView imageView,profile_pic_layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        otherUser = AndroidUtils.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtils.getChatroomId(FirebaseUtils.currentUserID(), otherUser.getUserId());
        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);
        imageView = findViewById(R.id.profile_pic_image_view);
        profile_pic_layout = findViewById(R.id.profile_pic_image_view);

        FirebaseUtils.getOtherProfilePicStorageRef(otherUser.getUserId()).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    AndroidUtils.setProfilePic(this,uri,profile_pic_layout);
                });
        backBtn.setOnClickListener((v) -> {
            onBackPressed();
        });
        otherUsername.setText(otherUser.getUsername());

        getOrCreateChatRoomModel();
        sendMessageBtn.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (message.isEmpty()) {
                return;
            }
            sendMessageToUser(message);
        });
        setupChatRecyclerView();
    }
    void setupChatRecyclerView(){
        Query query = FirebaseUtils.getChatroomMessageReference(chatroomId).orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>().setQuery(query, ChatMessage.class).build();
        adapter = new ChatRecyclerAdapter(options, getApplicationContext(),otherUser);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }
    void sendMessageToUser(String message) {
        chatroom.setLastMessageTimestamp(Timestamp.now());
        chatroom.setLastMessageSenderId(FirebaseUtils.currentUserID());
        chatroom.setLastMessage(message);
        FirebaseUtils.getChatroomReference(chatroomId).set(chatroom);

        ChatMessage chatMessage = new ChatMessage(message, FirebaseUtils.currentUserID(), Timestamp.now());
        FirebaseUtils.getChatroomMessageReference(chatroomId).add(chatMessage)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            messageInput.setText("");
                        }
                    }
                });
    }

    void getOrCreateChatRoomModel() {
        FirebaseUtils.getChatroomReference(chatroomId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    chatroom = task.getResult().toObject(Chatroom.class);
                    if (chatroom == null) {
                        chatroom = new Chatroom(
                                chatroomId,
                                Arrays.asList(FirebaseUtils.currentUserID(), otherUser.getUserId()),
                                Timestamp.now(),
                                "","");
                        FirebaseUtils.getChatroomReference(chatroomId).set(chatroom);
                    }
                }

            }
        });
    }

}
// test