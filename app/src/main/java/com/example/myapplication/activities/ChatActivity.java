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

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

        ChatMessage chatMessage = new ChatMessage(message, FirebaseUtils.currentUserID(), Timestamp.now(),"");
        FirebaseUtils.getChatroomMessageReference(chatroomId).add(chatMessage)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            messageInput.setText("");
                            sendNotification(message);
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
    void sendNotification(String message){

        FirebaseUtils.currentUserDetail().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                User currentUser = task.getResult().toObject(User.class);
                try{
                    JSONObject jsonObject  = new JSONObject();

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title",currentUser.getUsername());
                    notificationObj.put("body",message);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("userId",currentUser.getUserId());

                    jsonObject.put("notification",notificationObj);
                    jsonObject.put("data",dataObj);
                    jsonObject.put("to",otherUser.getFcmToken());

                    callApi(jsonObject);


                }catch (Exception e){

                }

            }
        });

    }

    void callApi(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer AAAAXyT2pOs:APA91bFIbzMmeFDpg0yO6S-jY30E65JsBtIHWUccoCB6G-W1-2NXrJk4T9Bg0VY86EiTXT3Nmmjt0nTiljOBKABNamumIshTwME_vra5U_H0Kz17qunBpHA8pQTEKnLY3BkkPdq0muOv")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }
}
// test