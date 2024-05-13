package com.example.myapplication.interfaces;

import com.example.myapplication.models.User;

public interface OnDataPassListener {
    void onDataPass(Integer data,boolean visible);
    void onFnUserFilterPass(User user);
}
