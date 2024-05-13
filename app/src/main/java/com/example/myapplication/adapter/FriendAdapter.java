package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.interfaces.AddFriend;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.AndroidUtils;
import com.example.myapplication.utils.Convert;
import com.example.myapplication.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<User> list = new ArrayList<>();
    private AddFriend addFriend;

    private Boolean isFriend = false;

    public List<User> getList() {
        return list;
    }

    String urlAvaUser;
    Context context;

    public FriendAdapter(Boolean isFriend, AddFriend addFriend, Context context) {
        this.isFriend = isFriend;
        this.addFriend = addFriend;
        this.context = context;
    }

    public FriendAdapter(AddFriend addFriend) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_horizontal_layout1, parent, false);
        return new FriendViewHolder1(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User user = list.get(position);
        if (user != null) {
            if (position != 0) {
                if (holder instanceof AddFriendViewHolder) {
                    FirebaseUtils.getOtherProfilePicStorageRef(user.getUserId()).getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                AndroidUtils.setProfilePic(context, uri, ((AddFriendViewHolder) holder).profile_pic_image_view);
                                ((AddFriendViewHolder) holder).profile_pic_image_view.setVisibility(View.VISIBLE);
                                ((AddFriendViewHolder) holder).tvAvatar.setVisibility(View.GONE);
                            })
                            .addOnFailureListener(e -> {
                                ((AddFriendViewHolder) holder).tvAvatar.setText(Convert.convertName(user.getUsername()));
                                ((AddFriendViewHolder) holder).profile_pic_image_view.setVisibility(View.GONE);
                                ((AddFriendViewHolder) holder).tvAvatar.setVisibility(View.VISIBLE);
                            });
                    ((AddFriendViewHolder) holder).tvName.setText(user.getUsername());
                    ((AddFriendViewHolder) holder).tvPhone.setText(user.getPhone());
                    ((AddFriendViewHolder) holder).btnAdd.setOnClickListener(v -> addFriend.onAddFriend(user.getUserId(), user.getUsername(), position));
                } else if (holder instanceof FriendViewHolder1) {
                    FirebaseUtils.getOtherProfilePicStorageRef(user.getUserId()).getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                AndroidUtils.setProfilePic(context, uri, ((FriendViewHolder1) holder).profile_pic_image_view);
                                ((FriendViewHolder1) holder).tvAvatar.setVisibility(View.GONE);
                                ((FriendViewHolder1) holder).profile_pic_image_view.setVisibility(View.VISIBLE);
                            })
                            .addOnFailureListener(e -> {
                                ((FriendViewHolder1) holder).tvAvatar.setText(Convert.convertName(user.getUsername()));
                                ((FriendViewHolder1) holder).profile_pic_image_view.setVisibility(View.GONE);
                                ((FriendViewHolder1) holder).tvAvatar.setVisibility(View.VISIBLE);
                            });
                    ((FriendViewHolder1) holder).tvName.setText(user.getUsername());
                    ((FriendViewHolder1) holder).itemView.setOnClickListener(v -> addFriend.onClick(user));
                }

            } else {
                if (holder instanceof AddFriendViewHolder) {
                    ((AddFriendViewHolder) holder).tvAvatar.setText(Convert.convertName(user.getUsername()));
                    ((AddFriendViewHolder) holder).tvName.setText(user.getUsername());
                    ((AddFriendViewHolder) holder).tvPhone.setText(user.getPhone());
                    ((AddFriendViewHolder) holder).btnAdd.setOnClickListener(v -> addFriend.onAddFriend(user.getUserId(), user.getUsername(), position));
                } else if (holder instanceof FriendViewHolder1) {
                    ((FriendViewHolder1) holder).tvAvatar.setText(Convert.convertName(user.getUsername()));
                    ((FriendViewHolder1) holder).tvName.setText(user.getUsername());
                    ((FriendViewHolder1) holder).itemView.setOnClickListener(v -> addFriend.onClick(user));
                }
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
        private ImageView profile_pic_image_view;

        public AddFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tv_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
            btnAdd = itemView.findViewById(R.id.btn_add);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            profile_pic_image_view = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }


    public class FriendViewHolder1 extends RecyclerView.ViewHolder {
        private TextView tvAvatar, tvName;
        private ImageButton btnUnFiend;
        private ImageView profile_pic_image_view;

        public FriendViewHolder1(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tv_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
            profile_pic_image_view = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }

}
