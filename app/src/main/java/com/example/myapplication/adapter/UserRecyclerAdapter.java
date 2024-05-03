package com.example.myapplication.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class UserRecyclerAdapter extends FirestoreRecyclerAdapter<User, UserRecyclerAdapter.listUserViewHolder> {

    Context context;
    private Set<Integer> selectedItems = new HashSet<>(); // Danh sách các item được chọn
    public UserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<User> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull listUserViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull User model) {
        holder.usernameText.setText(model.getUsername());
        final int currentPosition = position;
        holder.itemView.setSelected(selectedItems.contains(currentPosition));
        if (selectedItems.contains(currentPosition)) {
            handleSelectedItem( holder.usernameText,holder.profilePic,true);
        } else {
            handleSelectedItem( holder.usernameText,holder.profilePic,false);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedItems.contains(currentPosition)) {
                    selectedItems.remove(currentPosition);
                    handleSelectedItem( holder.usernameText,holder.profilePic,false);
                } else {
                    selectedItems.add(currentPosition);
                    handleSelectedItem( holder.usernameText,holder.profilePic,true);

                }
                notifyItemChanged(currentPosition);
            }
        });
    }

    @NonNull
    @Override
    public listUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new listUserViewHolder(view);
    }

    class listUserViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        ShapeableImageView profilePic;


        public listUserViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
            usernameText = itemView.findViewById(R.id.username);
        }
    }
    void handleSelectedItem(TextView usernameText , ShapeableImageView profilePic, boolean isSelected){
        if(isSelected){
            usernameText.setTextColor(Color.parseColor("#EEAB01"));
            profilePic.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#EEAB01")));

        }
        else{
            usernameText.setTextColor(Color.parseColor("#99555453"));
            profilePic.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#99555453")));
        }

    }
}