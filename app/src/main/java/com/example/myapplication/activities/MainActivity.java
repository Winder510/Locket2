package com.example.myapplication.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ViewPagerAdapter;
import com.example.myapplication.interfaces.OnBackToCameraFragmentListener;

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
    }

    @Override
    public void onBackToCameraFragment() {
        viewPager2.setCurrentItem(0);
    }

}
