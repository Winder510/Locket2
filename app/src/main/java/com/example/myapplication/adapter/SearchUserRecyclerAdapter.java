package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activities.ChatActivity;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.AndroidUtils;
import com.example.myapplication.utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<User, SearchUserRecyclerAdapter.UserModelViewHolder> {
    Context context;

    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<User> options, Context context) {
        super(options);
        this.context = context;
    }
    //Phương thức này được gọi mỗi khi RecyclerView cần hiển thị một mục trong danh sách. Nó cập nhật nội dung của ViewHolder để phản ánh dữ liệu người dùng tại vị trí cụ thể trong danh sách.

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull User model) {
        holder.usernameText.setText(model.getUsername());
        holder.phoneText.setText(model.getPhone());
        if(model.getUserId().equals(FirebaseUtils.currentUserID())){
            holder.usernameText.setText(model.getUsername()+"(Me)");
        }
        holder.itemView.setOnClickListener(v->{
            // navigate to chat activity
            Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtils.passUserModelAsIntent(intent,model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }
    //Phương thức này tạo một ViewHolder mới khi RecyclerView cần. Nó inflate (nạp) layout cho mỗi mục của danh sách từ một tệp XML và trả về một instance của ViewHolder chứa nó.
    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row,parent,false);
       return new UserModelViewHolder(view);
    }

    //Đây là một inner class được sử dụng để đại diện cho mỗi mục trong RecyclerView. Nó chứa các tham chiếu đến các thành phần giao diện người dùng (TextView, ImageView) trong mỗi mục.

    class UserModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView phoneText;
        ImageView profilePic;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            phoneText = itemView.findViewById(R.id.phone_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
        }
    }
}
