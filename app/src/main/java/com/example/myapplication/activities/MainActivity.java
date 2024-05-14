package com.example.myapplication.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ViewPagerAdapter;
import com.example.myapplication.interfaces.OnBackToCameraFragmentListener;
import com.example.myapplication.utils.FirebaseUtils;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity implements OnBackToCameraFragmentListener {
    ViewPager2 viewPager2;
    ViewPagerAdapter adapter;
    Boolean canSwipe = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager2 = findViewById(R.id.viewpager2);
        adapter = new ViewPagerAdapter(this,canSwipe);
        viewPager2.setAdapter(adapter);
        getFCMToken();
    }

    @Override
    public void onBackToCameraFragment() {
        viewPager2.setCurrentItem(0);
    }
    void getFCMToken()
    {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String token = task.getResult();
                FirebaseUtils.currentUserDetail().update("fcmToken",token);

            }
        });
    }

}

