package com.example.myapplication.activities;
import static android.content.ContentValues.TAG;

import com.example.myapplication.Gesture.SimpleGestureFilter;
import com.example.myapplication.Gesture.SimpleGestureFilter.SimpleGestureListener;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;

import com.google.firebase.firestore.FirebaseFirestoreException;

import maes.tech.intentanim.CustomIntent;

public class SettingsActivity extends AppCompatActivity implements
        SimpleGestureListener {
    private SimpleGestureFilter detector;
    ImageView profilePic;
    TextView usernameInput;

    Button logoutBtn, profilePicBtn, editNameBtn, editBdayBtn, listBlockBtn, editMailBtn, editReportBtn, editSuggestionBtn,
            openTiktokBtn, openIgBtn, openTwitterBtn, openServiceBtn, openPolicyBtn;

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
        logoutBtn = findViewById(R.id.logout_btn);
        btnBack = findViewById(R.id.back_btn);
        profilePicBtn = findViewById(R.id.btnProfile_Image);
        usernameInput = findViewById(R.id.profile_username);
        editNameBtn = findViewById(R.id.btnEditName);
        editBdayBtn = findViewById(R.id.btnEditBday);
        listBlockBtn = findViewById(R.id.btnListBlock);
        editMailBtn = findViewById(R.id.btnEditMail);
        editReportBtn = findViewById(R.id.btnEditReport);
        editSuggestionBtn = findViewById(R.id.btnEditSuggestion);
        openTiktokBtn = findViewById(R.id.btnOpenTiktok);
        openIgBtn = findViewById(R.id.btnOpenIG);
        openTwitterBtn = findViewById(R.id.btnOpenTwitter);
        openServiceBtn = findViewById(R.id.btnOpenService);
        openPolicyBtn = findViewById(R.id.btnOpenPolicy);




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

        setUpUserName(currentUser.getUsername());


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

        editMailBtn.setOnClickListener(v -> showEditDialog(R.layout.edit_email));
        editNameBtn.setOnClickListener(v->showEditDialog(R.layout.edit_name));
        editBdayBtn.setOnClickListener(v->showEditDialog(R.layout.edit_birthday));
        listBlockBtn.setOnClickListener(v->showEditDialog(R.layout.edit_block));
        editReportBtn.setOnClickListener(v->showEditDialog(R.layout.edit_report));
        editSuggestionBtn.setOnClickListener(v->showEditDialog(R.layout.edit_suggestion));
        openTiktokBtn.setOnClickListener(v->openLink("https://www.tiktok.com/@locketcamera"));
        openIgBtn.setOnClickListener(v->openLink("https://www.instagram.com/locketcamera/"));
        openTwitterBtn.setOnClickListener(v->openLink("https://twitter.com/locketcamera"));
        openServiceBtn.setOnClickListener(v->openLink("https://locket.camera/terms"));
        openPolicyBtn.setOnClickListener(v->openLink("https://locket.camera/privacy"));
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
                onBackPressed();
                CustomIntent.customType(SettingsActivity.this, "left-to-right");
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
    public void showEditDialog(int layoutResId) {
        BottomSheetSetting bottomSheetSetting = new BottomSheetSetting(layoutResId);
        bottomSheetSetting.show(getSupportFragmentManager(),"TAG");
    }
    public void openLink(String Link)
    {
        String url = Link;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
    public void setUpUserName(String newName){
        usernameInput.setText(newName);
    }
    public void updateToFireStore() {
        String userId = FirebaseUtils.currentUserID();
        if (userId != null) {
            DocumentReference userRef = FirebaseUtils.allUserCollectionReference().document(userId); // Tham chiếu đến tài liệu người dùng
            userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String newName = documentSnapshot.getString("username");
                        // Gọi phương thức setUpUserName để cập nhật giao diện
                        setUpUserName(newName);
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });
        }
    }
}