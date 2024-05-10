package com.example.myapplication.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;
import java.util.Set;
public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.UserViewHolder> {

    private final ArrayList<User> userList;
    private final Context context;
    private final Set<Integer> selectedItems = new HashSet<>(); // Danh sách các phần tử được chọn

    public UserRecyclerAdapter(ArrayList<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
        if (selectedItems.isEmpty() && !userList.isEmpty()) {
            selectedItems.add(0); // Thêm vị trí của phần tử đầu tiên vào danh sách selectedItems
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, @SuppressLint("RecyclerView") int position) {
        User user = userList.get(position);
        holder.usernameText.setText(user.getUsername());

        if (selectedItems.contains(position)) {
            handleSelectedItem(holder.usernameText, holder.profilePic, true);
        } else {
            handleSelectedItem(holder.usernameText, holder.profilePic, false);
        }

        // Xử lý sự kiện khi người dùng chọn một item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                if (selectedItems.contains(position)) {
                    selectedItems.remove(position);
                } else {
                    if(position==0)
                    {
                        selectedItems.removeAll(selectedItems);
                        selectedItems.add(position);
                    }else{
                        selectedItems.remove(0);
                        selectedItems.add(position);
                    }
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        ShapeableImageView profilePic;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
            usernameText = itemView.findViewById(R.id.username);
        }
    }

    void handleSelectedItem(TextView usernameText, ShapeableImageView profilePic, boolean isSelected) {
        if (isSelected) {
            usernameText.setTextColor(Color.parseColor("#EEAB01"));
            profilePic.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#EEAB01")));
        } else {
            usernameText.setTextColor(Color.parseColor("#99555453"));
            profilePic.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#99555453")));
        }
    }

    public ArrayList<String> getIdAllowedFriend() {
        ArrayList<String> tmp = new ArrayList<>();
        for (Integer number : selectedItems) {
          tmp.add(userList.get(number).getUserId());
        }
        return  tmp;
    }
}
