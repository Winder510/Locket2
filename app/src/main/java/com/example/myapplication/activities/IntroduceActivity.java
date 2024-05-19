package com.example.myapplication.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

@SuppressLint("CustomSplashScreen")
public class IntroduceActivity extends AppCompatActivity {
    Button startButton;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);
        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> {
            startActivity(new Intent(IntroduceActivity.this,LoginPhoneNumberActivity.class));
        });

    }
}
