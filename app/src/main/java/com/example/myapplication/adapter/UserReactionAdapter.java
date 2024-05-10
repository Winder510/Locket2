package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.models.UserReaction;
import com.example.myapplication.utils.AndroidUtils;
import com.example.myapplication.utils.FirebaseUtils;

import java.util.List;

public class UserReactionAdapter extends RecyclerView.Adapter<UserReactionAdapter.UserViewHolder>{

    private Context mContext;
    private List<UserReaction> mListUser;
    public UserReactionAdapter(Context mContext) {
        this.mContext = mContext;
    }
    public void setData(List<UserReaction> list){
        this.mListUser = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_reaction,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserReaction user = mListUser.get(position);
        if(user == null){
            return;
        }
        FirebaseUtils.getOtherProfilePicStorageRef(user.getUid()).getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    AndroidUtils.setProfilePic(mContext,uri,holder.imgUser);
                });
        holder.tvName.setText(user.getName());
        holder.reactType.setImageResource(user.getReactionType());
    }

    @Override
    public int getItemCount() {
        if(mListUser != null){
            return mListUser.size();
        }
        return 0;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        private ImageView imgUser;
        private TextView tvName;
        private ImageView reactType;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            imgUser = itemView.findViewById(R.id.img_user);
            tvName = itemView.findViewById(R.id.userName);
            reactType = itemView.findViewById(R.id.reactionType);
        }
    }
}
