package com.example.myapplication.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ChatRecyclerAdapter;
import com.example.myapplication.adapter.UserRecyclerAdapter;
import com.example.myapplication.models.ChatMessage;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class UploadActivity extends AppCompatActivity {
    UserRecyclerAdapter adapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("imagePath")) {

            String imagePath = intent.getStringExtra("imagePath");

            ImageView imagePreview = findViewById(R.id.imagePreview);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

            Matrix matrix = new Matrix();
            matrix.postRotate(90);

            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            imagePreview.setImageBitmap(rotatedBitmap);
        }


        recyclerView = findViewById(R.id.list_user_recycler_view);

        setupChatRecyclerView();

    }
    void setupChatRecyclerView(){
       Query query = FirebaseUtils.allUserCollectionReference();
       FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>().setQuery(query,User.class).build();
        adapter = new UserRecyclerAdapter(options,getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
