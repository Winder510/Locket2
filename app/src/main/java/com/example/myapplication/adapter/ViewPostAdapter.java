package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.Post;

import java.util.ArrayList;

public class ViewPostAdapter
        extends RecyclerView.Adapter<ViewPostAdapter
        .ViewHolder> {
    ArrayList<Post> postItemArrayList;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    public ViewPostAdapter
            (ArrayList<Post> postItemArrayList) {
        this.postItemArrayList = postItemArrayList;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post postItem = postItemArrayList.get(position);
//        holder.imageView.setImageResource(postItem.imageID);
//        holder.desc.setText(postItem.desc);
    }

    @Override
    public int getItemCount() {
        return postItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView desc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            imageView = itemView.findViewById(R.id.imageview);
//            desc = itemView.findViewById(R.id.textview);
        }
    }
}
