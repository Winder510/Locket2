package com.example.myapplication.activities;
import com.example.myapplication.Gesture.SimpleGestureFilter;
import com.example.myapplication.Gesture.SimpleGestureFilter.SimpleGestureListener;


import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.AndroidUtils;
import com.example.myapplication.utils.FirebaseUtils;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.util.HashMap;
import java.util.Map;

import maes.tech.intentanim.CustomIntent;

public class SettingsActivity extends AppCompatActivity implements
        SimpleGestureListener {
    private SimpleGestureFilter detector;
    ImageView profilePic;
    TextView usernameInput;
    Button logoutBtn, profilePicBtn, editNameBtn, editBdayBtn, listBlockBtn, editMailBtn;

    ActivityResultLauncher<Intent> imagePickerLauncher;
    Uri selectedImageUri;
    ImageButton btnBack;
    User currentUser;
    View mainView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        currentUser = AndroidUtils.getUserModelFromIntent(getIntent());

        profilePic = findViewById(R.id.profile_image_view);
        usernameInput = findViewById(R.id.profile_username);
        logoutBtn = findViewById(R.id.logout_btn);
        btnBack = findViewById(R.id.back_btn);
        profilePicBtn = findViewById(R.id.btnProfile_Image);
        editNameBtn = findViewById(R.id.btnEditName);
        editBdayBtn = findViewById(R.id.btnEditBday);
        listBlockBtn = findViewById(R.id.btnListBlock);
        editMailBtn = findViewById(R.id.btnEditMail);





        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            AndroidUtils.setProfilePic(getApplicationContext(), selectedImageUri, profilePic);
                        }
                    }
                });

        usernameInput.setText(currentUser.getUsername());
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUtils.logout();
                Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                CustomIntent.customType(SettingsActivity.this, "left-to-right");
            }
        });
        profilePic.setOnClickListener(v -> pickImage());
        profilePicBtn.setOnClickListener(v -> pickImage());

        if (selectedImageUri != null) {
            FirebaseUtils.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                        AndroidUtils.showToast(getApplicationContext(),"hehe");
                        updateToFireStore();
                    });
        } else {
            updateToFireStore();
        }
        detector = new SimpleGestureFilter(SettingsActivity.this, this);

        editMailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheet bottomSheet = new BottomSheet();
                bottomSheet.show(getSupportFragmentManager(),"TAG");
            }
        });
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
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
                break;
            case SimpleGestureFilter.SWIPE_UP:
                break;
        }
    }
    public void pickImage() {
        ImagePicker.with(this)
                .cropSquare()
                .compress(512)
                .maxResultSize(512, 512)
                .createIntent(intent -> {
                    imagePickerLauncher.launch(intent);
                    return null;
                });
    }
    void updateToFireStore() {

    }

}