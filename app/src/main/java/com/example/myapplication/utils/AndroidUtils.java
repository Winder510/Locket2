package com.example.myapplication.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.models.User;

public class AndroidUtils {
    public static void showToast(Context context, String mes){
        Toast.makeText(context,mes,Toast.LENGTH_LONG).show();

    }

    public static void passUserModelAsIntent(Intent intent, User model) {
        intent.putExtra("username", model.getUsername());
        intent.putExtra("phone", model.getPhone());
        intent.putExtra("userId", model.getUserId());

    }

    public static User getUserModelFromIntent(Intent intent) {
        User user = new User();
        user.setUsername(intent.getStringExtra("username"));
        user.setPhone(intent.getStringExtra("phone"));
        user.setUserId(intent.getStringExtra("userId"));
        return user;
    }
    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}
