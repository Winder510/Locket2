package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.interfaces.AddFriend;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.Convert;

import java.util.ArrayList;
import java.util.List;

public class AddFriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<User> list = new ArrayList<>();
    private AddFriend addFriend;

    private Boolean isFriend = false;

    public List<User> getList() {
        return list;
    }

    public AddFriendAdapter(Boolean isFriend, AddFriend addFriend) {
        this.isFriend = isFriend;
        this.addFriend = addFriend;
    }

    public AddFriendAdapter(AddFriend addFriend) {
        this.addFriend = addFriend;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<User> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void addItem(User user) {
        list.add(user);
        notifyItemInserted(list.size() - 1);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeItemAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (isFriend) return 1;
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_friend_layout, parent, false);
            return new AddFriendViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_horizontal_layout, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User user = list.get(position);
        if (user != null) {
            if (holder instanceof AddFriendViewHolder) {
                ((AddFriendViewHolder) holder).tvAvatar.setText(Convert.convertName(user.getUsername()));
                ((AddFriendViewHolder) holder).tvName.setText(user.getUsername());
                ((AddFriendViewHolder) holder).tvPhone.setText(user.getPhone());
                ((AddFriendViewHolder) holder).btnAdd.setOnClickListener(v -> addFriend.onAddFriend(user.getUserId(), user.getUsername(), position));
            } else if (holder instanceof FriendViewHolder) {
                ((FriendViewHolder) holder).tvAvatar.setText(Convert.convertName(user.getUsername()));
                ((FriendViewHolder) holder).tvName.setText(user.getUsername());
                ((FriendViewHolder) holder).btnUnFiend.setOnClickListener(v -> addFriend.unFriend(user.getUserId(), position));
                ((FriendViewHolder) holder).itemView.setOnClickListener(v -> addFriend.onClick(user));
            }
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class AddFriendViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAvatar, tvName, tvPhone;
        private Button btnAdd;

        public AddFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tv_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
            btnAdd = itemView.findViewById(R.id.btn_add);
            tvPhone = itemView.findViewById(R.id.tv_phone);
        }
    }


    public class FriendViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAvatar, tvName;
        private ImageButton btnUnFiend;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tv_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
            btnUnFiend = itemView.findViewById(R.id.btn_un_friend);
        }
    }
}
