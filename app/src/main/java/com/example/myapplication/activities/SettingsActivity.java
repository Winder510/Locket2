package com.example.myapplication.activities;
import com.example.myapplication.Gesture.SimpleGestureFilter;
import com.example.myapplication.Gesture.SimpleGestureFilter.SimpleGestureListener;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import maes.tech.intentanim.CustomIntent;

public class SettingsActivity extends AppCompatActivity implements
        SimpleGestureListener {
    private SimpleGestureFilter detector;
    ImageView profilePic;
    TextView usernameInput;
    Button updateProfileBtn, logoutBtn;

    ActivityResultLauncher<Intent> imagePickerLauncher;
    Uri selectedImageUri;
    ImageButton btnBack;
    User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        currentUser = AndroidUtils.getUserModelFromIntent(getIntent());

        profilePic = findViewById(R.id.profile_image_view);
        usernameInput = findViewById(R.id.profile_username);
        updateProfileBtn = findViewById(R.id.update_profile);
        logoutBtn = findViewById(R.id.logout_btn);
        btnBack = findViewById(R.id.back_btn);
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
        profilePic.setOnClickListener(v -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                    .createIntent(new Function1<Intent, Unit>() {
                        @Override
                        public Unit invoke(Intent intent) {
                            imagePickerLauncher.launch(intent);
                            return null;
                        }
                    });
        });
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
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {

        //Detect the swipe gestures and display toast
        String showToastMessage = "";

        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT:
                showToastMessage = "You have Swiped Right.";
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                showToastMessage = "You have Swiped Left.";
                onBackPressed();
                CustomIntent.customType(SettingsActivity.this, "left-to-right");
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
                showToastMessage = "You have Swiped Down.";
                break;
            case SimpleGestureFilter.SWIPE_UP:
                showToastMessage = "You have Swiped Up.";
                break;

        }
        Toast.makeText(this, showToastMessage, Toast.LENGTH_SHORT).show();
    }

    void updateToFireStore() {

    }

}