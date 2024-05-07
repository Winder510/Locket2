package com.example.myapplication.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {
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
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
