package com.example.myapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.fragments.AllPostFragment;
import com.example.myapplication.fragments.ViewPostFragment;
import com.example.myapplication.interfaces.OnItemClickListener;
import com.example.myapplication.models.Post;

import java.util.List;


public class AllPostAdapter
        extends RecyclerView.Adapter<AllPostAdapter
        .ViewHolder> {
    List<Post> posts;

    Context context;
    public OnItemClickListener mListener;

    public AllPostAdapter(List<Post> posts, Context context, OnItemClickListener listener) {
        this.posts = posts;
        this.context = context;
        this.mListener = listener;
    }

    public AllPostAdapter(List<Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_image_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Post post = posts.get(position);

        Glide.with(holder.imageView)
                .load(post.getPostImg_url())
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.postImage);

        }
    }
}
