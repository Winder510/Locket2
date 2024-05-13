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
import com.example.myapplication.models.Post;
import com.example.myapplication.utils.AndroidUtils;
import com.example.myapplication.utils.FirebaseUtils;

import java.util.List;

public class ViewPostAdapter
        extends RecyclerView.Adapter<ViewPostAdapter
        .ViewHolder> {
    List<Post> posts;
    Context context;

    public ViewPostAdapter(List<Post> posts, Context context) {
        this.posts = posts; this.context=context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);

        Glide.with(holder.imageView)
                .load(post.getPostImg_url())
                .into(holder.imageView);
        if (!post.getPostCaption().isEmpty()){
            holder.captionText.setText(post.getPostCaption());
            holder.captionText.setVisibility(View.VISIBLE);
        } else {
         holder.captionText.setVisibility(View.GONE);
        }
        FirebaseUtils.getUserInfor(post.getUserId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        if(documentSnapshot.getString("userId").equals(FirebaseUtils.currentUserID())){
                            holder.userNameTextview.setText("Bạn");
                        }
                        else {
                            String userName = documentSnapshot.getString("username");
                            String userId = documentSnapshot.getString("userId");
                            FirebaseUtils.getOtherProfilePicStorageRef(userId).getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        AndroidUtils.setProfilePic(context,uri,holder.profile_pic_image_view);
                                    });
                            holder.userNameTextview.setText(userName);
                        }
                    }
                });



    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView,profile_pic_image_view;
        TextView captionText, userNameTextview;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview);
            captionText = itemView.findViewById(R.id.statusText);
            userNameTextview = itemView.findViewById(R.id.userNameTextview);
            profile_pic_image_view = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
