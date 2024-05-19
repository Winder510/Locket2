package com.example.myapplication.activities;

import static android.content.ContentValues.TAG;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.BottomSheetDialog.BottomSheetSetting;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.AndroidUtils;
import com.example.myapplication.utils.FirebaseUtils;
import com.github.dhaval2404.imagepicker.ImagePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.messaging.FirebaseMessaging;

import maes.tech.intentanim.CustomIntent;

public class SettingsActivity extends AppCompatActivity implements SimpleGestureListener {
    private SimpleGestureFilter detector;
    ImageView profilePic;
    TextView usernameInput;

    Button logoutBtn, profilePicBtn, editNameBtn, editBdayBtn, listBlockBtn, editMailBtn, editReportBtn, editSuggestionBtn,
            openTiktokBtn, openIgBtn, openTwitterBtn, openServiceBtn, openPolicyBtn;

    ActivityResultLauncher<Intent> imagePickerLauncher;
    Uri selectedImageUri;
    ImageButton btnBack;
    User currentUser;
    Button btnnsonguoiban;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

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
        btnnsonguoiban=findViewById(R.id.btnnguoiban);

        detector = new SimpleGestureFilter(SettingsActivity.this, this);

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            AndroidUtils.setProfilePic(getApplicationContext(), selectedImageUri, profilePic);
                            if (selectedImageUri != null) {
                                FirebaseUtils.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
                                        .addOnCompleteListener(task -> {
                                            AndroidUtils.showToast(getApplicationContext(), "hehe");
                                            FirebaseUtils.getCurrentProfilePicStorageRef().getDownloadUrl()
                                                    .addOnSuccessListener(uri -> {
                                                        // Cập nhật URL của ảnh vào tài liệu người dùng trong Firestore
                                                        FirebaseUtils.allUserCollectionReference().document(FirebaseUtils.currentUserID())
                                                                .update("profilePicUrl", uri.toString())
                                                                .addOnSuccessListener(aVoid -> {
                                                                    AndroidUtils.showToast(getApplicationContext(), "Profile picture updated successfully");
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    AndroidUtils.showToast(getApplicationContext(), "Failed to update profile picture");
                                                                });
                                                    });
                                        });
                            }
                        }
                    }
                });

        FirebaseUtils.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    AndroidUtils.setProfilePic(this, uri, profilePic);
                });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseUtils.logout();
                            Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                });
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

        editMailBtn.setOnClickListener(v -> showEditDialog(R.layout.edit_email));
        editNameBtn.setOnClickListener(v -> showEditDialog(R.layout.edit_name));
        editBdayBtn.setOnClickListener(v -> showEditDialog(R.layout.edit_birthday));
        listBlockBtn.setOnClickListener(v -> showEditDialog(R.layout.edit_block));
        editReportBtn.setOnClickListener(v -> showEditDialog(R.layout.edit_report));
        editSuggestionBtn.setOnClickListener(v -> showEditDialog(R.layout.edit_suggestion));
        openTiktokBtn.setOnClickListener(v -> openLink("https://www.tiktok.com/@locketcamera"));
        openIgBtn.setOnClickListener(v -> openLink("https://www.instagram.com/locketcamera/"));
        openTwitterBtn.setOnClickListener(v -> openLink("https://twitter.com/locketcamera"));
        openServiceBtn.setOnClickListener(v -> openLink("https://locket.camera/terms"));
        openPolicyBtn.setOnClickListener(v -> openLink("https://locket.camera/privacy"));

        detector = new SimpleGestureFilter(SettingsActivity.this, this);
        updateToFireStore();

        // Gọi phương thức để lấy thông tin người dùng
        fetchCurrentUser();
    }

    private void fetchCurrentUser() {
        String userId = FirebaseUtils.currentUserID();
        if (userId != null) {
            DocumentReference userRef = FirebaseUtils.allUserCollectionReference().document(userId);
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            currentUser = document.toObject(User.class);
                            if (currentUser != null) {
                                // Tiếp tục các hành động cần thực hiện với currentUser
                                setUpUserName(currentUser.getUsername());
                                btnnsonguoiban.setText(String.valueOf(currentUser.CountFriend()) + " người bạn");
                            }
                        }
                    } else {
                        Log.w(TAG, "Error getting user details.", task.getException());
                    }
                }
            });
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
        bottomSheetSetting.show(getSupportFragmentManager(), "TAG");
    }

    public void openLink(String link) {
        String url = link;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    public void setUpUserName(String newName) {
        usernameInput.setText(newName);
    }

    public void updateToFireStore() {
        String userId = FirebaseUtils.currentUserID();
        if (userId != null) {
            DocumentReference userRef = FirebaseUtils.allUserCollectionReference().document(userId);
            userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        return;
                    }
                    if (value != null && value.exists()) {
                        String updatedName = value.getString("username");
                        if (updatedName != null) {
                            setUpUserName(updatedName);
                        }
                        currentUser = value.toObject(User.class);
                        if (currentUser != null) {
                            btnnsonguoiban.setText(String.valueOf(currentUser.CountFriend()) + " người bạn");
                        }
                    }
                }
            });
        }
    }
}
