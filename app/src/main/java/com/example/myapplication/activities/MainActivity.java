package com.example.myapplication.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ViewPagerAdapter;
import com.example.myapplication.interfaces.ReactionListener;

public class MainActivity extends AppCompatActivity implements OnBackToCameraFragmentListener,ReactionListener {
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

    @Override
    public void onReactionSelected(int reactionType) {
        switch (reactionType){
            case 0:
                Toast.makeText(MainActivity.this, "Like", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(MainActivity.this, "Love", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(MainActivity.this, "LoveLove", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(MainActivity.this, "Wow", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                Toast.makeText(MainActivity.this, "Haha", Toast.LENGTH_SHORT).show();
                break;
            case 5:
                Toast.makeText(MainActivity.this, "Sad", Toast.LENGTH_SHORT).show();
                break;
            case 6:
                Toast.makeText(MainActivity.this, "Angry", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
