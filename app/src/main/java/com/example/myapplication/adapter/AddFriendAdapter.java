package com.example.myapplication.adapter;

import static android.icu.text.DisplayContext.LENGTH_SHORT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class AddFriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<User> list = new ArrayList<>();
    private AddFriend addFriend;

    private Boolean isFriend = false;

    public List<User> getList() {
        return list;
    }
    Context context;

    public int getNumberofFriend(){
        return list.size();
    }
    public AddFriendAdapter(Boolean isFriend, AddFriend addFriend, Context context) {
        this.isFriend = isFriend;
        this.addFriend = addFriend;
        this.context = context;
    }

    public AddFriendAdapter(AddFriend addFriend,Context context) {
        this.addFriend = addFriend;
        this.context = context;
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
                FirebaseUtils.getOtherProfilePicStorageRef(user.getUserId()).getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            // Nếu có URL ảnh đại diện, set ảnh cho holder.profilePic
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

                final String[] name = new String[1];
                FirebaseUtils.getUserName(new FirebaseUtils.UserNameCallback() {
                    @Override
                    public void onUserNameReceived(String userName) {
                        name[0] = userName;
                    }
                });

                ((AddFriendViewHolder) holder).btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uname = name[0];
                        String usname = user.getUsername();
                        if (uname.equals(usname)) {
                            Toast.makeText(v.getContext(), "Không thể thêm bản thân làm bạn bè",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            int adapterPosition = holder.getAdapterPosition();
                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                addFriend.onAddFriend(user.getUserId(), user.getUsername(), adapterPosition);
                            }
                        }
                    }
                });
            } else if (holder instanceof FriendViewHolder) {
                FirebaseUtils.getOtherProfilePicStorageRef(user.getUserId()).getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            // Nếu có URL ảnh đại diện, set ảnh cho holder.profilePic
                            AndroidUtils.setProfilePic(context, uri,  ((FriendViewHolder) holder).profile_pic_image_view);
                            ((FriendViewHolder) holder).profile_pic_image_view.setVisibility(View.VISIBLE);
                            ((FriendViewHolder) holder).tvAvatar.setVisibility(View.GONE);
                        })
                        .addOnFailureListener(e -> {
                            ((FriendViewHolder) holder).tvAvatar.setText(Convert.convertName(user.getUsername()));
                            ((FriendViewHolder) holder).profile_pic_image_view.setVisibility(View.GONE);
                            ((FriendViewHolder) holder).tvAvatar.setVisibility(View.VISIBLE);
                        });
                ((FriendViewHolder) holder).tvName.setText(user.getUsername());

                ((FriendViewHolder) holder).btnUnFiend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int adapterPosition = holder.getAdapterPosition();
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            addFriend.unFriend(user.getUserId(), adapterPosition);
                        }
                    }
                });

                ((FriendViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addFriend.onClick(user);
                    }
                });
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
        ImageView profile_pic_image_view;

        public AddFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tv_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
            btnAdd = itemView.findViewById(R.id.btn_add);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            profile_pic_image_view= itemView.findViewById(R.id.profile_pic_image_view);
        }
    }


    public class FriendViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAvatar, tvName;
        private ImageButton btnUnFiend;
        ImageView profile_pic_image_view;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tv_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
            btnUnFiend = itemView.findViewById(R.id.btn_un_friend);
            profile_pic_image_view= itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
