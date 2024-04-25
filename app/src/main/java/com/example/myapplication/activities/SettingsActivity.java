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
    Button logoutBtn, profilePicBtn, editNameBtn, editBdayBtn, listBlockBtn;

    ActivityResultLauncher<Intent> imagePickerLauncher;
    Uri selectedImageUri;
    ImageButton btnBack;
    User currentUser;
    View viewEdit_Name,viewEdit_Bday,viewList_Block, mainView;
    private Map<View, Boolean> viewSlideStates = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        currentUser = AndroidUtils.getUserModelFromIntent(getIntent());

        mainView = findViewById(R.id.settingsLayout);
        profilePic = findViewById(R.id.profile_image_view);
        usernameInput = findViewById(R.id.profile_username);
        logoutBtn = findViewById(R.id.logout_btn);
        btnBack = findViewById(R.id.back_btn);
        profilePicBtn = findViewById(R.id.btnProfile_Image);
        editNameBtn = findViewById(R.id.btnEditName);
        editBdayBtn = findViewById(R.id.btnEditBday);
        listBlockBtn = findViewById(R.id.btnListBlock);

        viewEdit_Name = findViewById(R.id.editName_View);
        viewEdit_Bday = findViewById(R.id.editBday_View);
        viewList_Block= findViewById(R.id.listBlock_View);

        viewSlideStates.put(viewEdit_Name, false);
        viewSlideStates.put(viewEdit_Bday, false);
        viewSlideStates.put(viewList_Block, false);



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

        editNameBtn.setOnClickListener(v->slideUp(viewEdit_Name));
        editBdayBtn.setOnClickListener(v->slideUp(viewEdit_Bday));
        listBlockBtn.setOnClickListener(v->slideUp(viewList_Block));
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
                handleSwipeRight();
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                handleSwipeLeft();
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
                handleSwipeDown();
                break;
            case SimpleGestureFilter.SWIPE_UP:
                handleSwipeUp();
                break;
        }
    }
    private void handleSwipeRight() {
        String showToastMessage = "You have Swiped Right.";
        Toast.makeText(this, showToastMessage, Toast.LENGTH_SHORT).show();
    }

    private void handleSwipeLeft() {
        String showToastMessage = "You have Swiped Left.";
        onBackPressed();
        CustomIntent.customType(SettingsActivity.this, "left-to-right");
    }

    private void handleSwipeDown() {
        if (viewSlideStates.containsValue(true)) { // Kiểm tra xem có view nào đang slide up không
            for (Map.Entry<View, Boolean> entry : viewSlideStates.entrySet()) {
                if (entry.getValue()) { // Nếu view đang slide up
                    slideDown(entry.getKey()); // Slide down view đó
                    viewSlideStates.put(entry.getKey(), false); // Đặt trạng thái của view đó là slide down
                    return;
                }
            }
        }
    }

    private void handleSwipeUp() {
        String showToastMessage = "You have Swiped Up.";
        Toast.makeText(this, showToastMessage, Toast.LENGTH_SHORT).show();
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
    public void slideUp(final View view) {
        view.setVisibility(View.VISIBLE);

        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);

        view.startAnimation(animate);

        ValueAnimator animator;

        if (view == viewEdit_Name || view == viewList_Block) {
            final int parentHeightInPx = ((ViewGroup) view.getParent()).getHeight();
            animator = ValueAnimator.ofInt(0, parentHeightInPx);
        }
        else {
            animator = ValueAnimator.ofInt(0, 900);
        }
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                view.getLayoutParams().height = value;
                view.requestLayout();
            }
        });
        animator.start();
        viewSlideStates.put(view, true);

        changeVGstate((ViewGroup) mainView, false);

    }

    public void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);

        changeVGstate((ViewGroup) mainView, true);
    }
    public static void changeVGstate(ViewGroup current, boolean enable)
    {
        current.setFocusable(enable);
        current.setClickable(enable);
        current.setEnabled(enable);

        for (int i = 0; i < current.getChildCount(); i++)
        {
            View v = current.getChildAt(i);
            if (v instanceof ViewGroup)
                changeVGstate((ViewGroup)v, enable);
            else
            {
                v.setFocusable(enable);
                v.setClickable(enable);
                v.setEnabled(enable);
            }
        }
    }
    void updateToFireStore() {

    }

}