package com.example.myapplication.adapter;

import android.content.Context;
import android.text.format.DateUtils;
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
import com.google.firebase.Timestamp;

import java.time.Instant;
import java.util.Date;
import java.util.List;

public class ViewPostAdapter
        extends RecyclerView.Adapter<ViewPostAdapter
        .ViewHolder> {
    List<Post> posts;
    Context context;

    public ViewPostAdapter(List<Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
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
        if (!post.getPostCaption().isEmpty()) {
            holder.captionText.setText(post.getPostCaption());
            holder.captionText.setVisibility(View.VISIBLE);
        } else {
            holder.captionText.setVisibility(View.GONE);
        }
        FirebaseUtils.getUserInfor(post.getUserId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        if (documentSnapshot.getString("userId").equals(FirebaseUtils.currentUserID())) {
                            holder.userNameTextview.setText("Bạn");
                        } else {
                            String userName = documentSnapshot.getString("username");
                            String userId = documentSnapshot.getString("userId");
                            FirebaseUtils.getOtherProfilePicStorageRef(userId).getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        AndroidUtils.setProfilePic(context, uri, holder.profile_pic_image_view);
                                    });
                            holder.userNameTextview.setText(userName);
                        }
                    }
                });
        // Tính toán và hiển thị thời gian đã đăng
        Date date = new Date(post.getCreated_at().getSeconds() * 1000); // Convert from seconds to milliseconds
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                date.getTime(),
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS
        );
        holder.timeTextView.setText(formatTimeAgo(timeAgo));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, profile_pic_image_view;
        TextView captionText, userNameTextview, timeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview);
            captionText = itemView.findViewById(R.id.statusText);
            userNameTextview = itemView.findViewById(R.id.userNameTextview);
            profile_pic_image_view = itemView.findViewById(R.id.profile_pic_image_view);
            timeTextView = itemView.findViewById(R.id.timeTextView);
        }
    }

    public String formatTimeAgo(CharSequence timeAgo) {
        String[] units = {"ph", "g", "ng"};
        String timeString = timeAgo.toString().trim(); // Loại bỏ khoảng trắng ở đầu và cuối chuỗi
        String timeValue="";
        int unitIndex = 0;
        if (timeString.endsWith("ago")) {
            timeString = timeString.substring(0, timeString.length() - 4);
            if (timeString.equals("0 minutes")) {
                return "Vừa xong";
            } else if (timeString.endsWith("minutes") || timeString.endsWith("minute")) {
                unitIndex = 0;
            } else if (timeString.endsWith("hours") || timeString.endsWith("hour")) {
                unitIndex = 1;
            } else if (timeString.endsWith("days") || timeString.endsWith("day")) {
                unitIndex = 2;
            }
            int spaceIndex = timeString.indexOf(" ");
            timeString = timeString.substring(0, spaceIndex);
            timeValue = timeString + units[unitIndex];
            return timeValue;
        }
        else {
            return timeString;
        }
    }
}
