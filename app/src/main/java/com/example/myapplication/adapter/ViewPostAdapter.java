package com.example.myapplication.adapter;

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
import com.example.myapplication.utils.FirebaseUtils;

import java.util.List;

public class ViewPostAdapter
        extends RecyclerView.Adapter<ViewPostAdapter
        .ViewHolder> {
    List<Post> posts;

    public ViewPostAdapter(List<Post> posts) {
        this.posts = posts;
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
                        String userName = documentSnapshot.getString("username");
                        holder.userNameTextview.setText(userName);
                    }
                });



    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView captionText, userNameTextview;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview);
            captionText = itemView.findViewById(R.id.statusText);
            userNameTextview = itemView.findViewById(R.id.userNameTextview);
        }
    }
}
