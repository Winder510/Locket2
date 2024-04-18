package com.example.myapplication.utils;

import android.content.Context;
import android.widget.Toast;

public class AndroidUtils {
    public static void showToast(Context context, String mes){
        Toast.makeText(context,mes,Toast.LENGTH_LONG).show();

    }
}
