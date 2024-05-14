package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.ChatMessage;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.AndroidUtils;
import com.example.myapplication.utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessage, RecyclerView.ViewHolder> {

    private static final int TYPE_SEND = 0;
    private static final int TYPE_RECEIVE_SINGLE = 1;
    private static final int TYPE_RECEIVE_GROUP = 2;
    private static final int TYPE_RECEIVE_REPLY_POST = 3;
    private static final int TYPE_SEND_REPLY_POST = 4;

    private User user;

    Context context;

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessage> options, Context context, User user) {
        super(options);
        this.context = context;
        this.user = user;
    }
    private boolean isLatestMessage(int position) {
        return position == getItemCount() - 1;
    }


    @Override
    public int getItemViewType(int position) {
        ChatMessage currentMessage = getItem(position);
        boolean isReceived = !currentMessage.getSenderId().equals(FirebaseUtils.currentUserID());
        if (currentMessage.getImageUrl().isEmpty()) {
            if (isReceived && isLatestMessage(position)) {
                return TYPE_RECEIVE_SINGLE; // This is a single message with avatar
            } else if (isReceived) {
                // Check if the previous message is from the same sender
                if (position > 0) {
                    ChatMessage previousMessage = getItem(position - 1);
                    if (previousMessage.getSenderId().equals(currentMessage.getSenderId())) {
                        return TYPE_RECEIVE_GROUP; // This is a group message without avatar
                    }
                }
                return TYPE_RECEIVE_SINGLE; // This is a single message with avatar
            } else {
                // Check if the next message is from the same sender
                if (position < getItemCount() - 1) {
                    ChatMessage nextMessage = getItem(position + 1);
                    if (nextMessage.getSenderId().equals(currentMessage.getSenderId())) {
                        return TYPE_SEND; // This is a group message
                    }
                }
                return TYPE_SEND; // This is a single message
            }
        } else {
            if (!isReceived) {
                return TYPE_RECEIVE_REPLY_POST; // Tin nhắn nhận có ảnh
            } else {
                return TYPE_SEND_REPLY_POST; // Tin nhắn gửi có ảnh
            }
        }
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_SEND:
                view = LayoutInflater.from(context).inflate(R.layout.send_chat_message_recycler_row, parent, false);
                return new ChatModelViewHolder(view);
            case TYPE_RECEIVE_SINGLE:
                view = LayoutInflater.from(context).inflate(R.layout.received_chat_message_recycler_row, parent, false);
                return new GroupChatModelViewHolder(view);
            case TYPE_RECEIVE_GROUP:
                view = LayoutInflater.from(context).inflate(R.layout.received_group_chat_message_recycler_row, parent, false);
                return new GroupChatModelViewHolder(view);
            case TYPE_SEND_REPLY_POST:
                view = LayoutInflater.from(context).inflate(R.layout.send_chat_with_image_recyler_row, parent, false);
                return new ReplyPostChatModelViewHolder(view);
            case TYPE_RECEIVE_REPLY_POST:
                view = LayoutInflater.from(context).inflate(R.layout.received_chat_with_image_recyler_row, parent, false);
                return new ReplyPostChatModelViewHolder(view);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull ChatMessage model) {
        if (holder.getItemViewType() == TYPE_SEND_REPLY_POST) {
            ReplyPostChatModelViewHolder viewHolder = (ReplyPostChatModelViewHolder) holder;
            viewHolder.nameTextView.setText("Bạn");
            if (!model.getImageCaption().isEmpty()) {
                viewHolder.statusTextView.setText(model.getImageCaption());
            }else{
                viewHolder.statusTextView.setVisibility(View.GONE);
            }
            viewHolder.dateTextView.setText(model.getImageDate());
            viewHolder.profile_pic_image_view.setVisibility(View.VISIBLE);
            FirebaseUtils.getOtherProfilePicStorageRef(model.getSenderId()).getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        AndroidUtils.setProfilePic(context, uri, viewHolder.profile_pic_image_view);
                    });
            Picasso.get().load(model.getImageUrl()).into(viewHolder.imagePost);
            viewHolder.left_chat_layout.setVisibility(View.VISIBLE);
            viewHolder.leftChatTextview.setText(model.getMessage());

        }
        if (holder.getItemViewType() == TYPE_RECEIVE_REPLY_POST) {

            ReplyPostChatModelViewHolder viewHolder = (ReplyPostChatModelViewHolder) holder;
            viewHolder.nameTextView.setText(user.getUsername());
            if (!model.getImageCaption().isEmpty()) {
                viewHolder.statusTextView.setText(model.getImageCaption());
            }else{
                viewHolder.statusTextView.setVisibility(View.GONE);
            }

            viewHolder.dateTextView.setText(model.getImageDate());
            viewHolder.profile_pic_image_view.setVisibility(View.GONE);
            Picasso.get().load(model.getImageUrl()).into(viewHolder.imagePost);
            viewHolder.left_chat_layout.setVisibility(View.VISIBLE);
            viewHolder.leftChatTextview.setText(model.getMessage());
        }
        if (holder.getItemViewType() == TYPE_SEND) {
            ChatModelViewHolder viewHolder = (ChatModelViewHolder) holder;
            viewHolder.rightChatLayout.setVisibility(View.VISIBLE);
            viewHolder.rightChatTextview.setText(model.getMessage());
        } else if (holder.getItemViewType() == TYPE_RECEIVE_SINGLE || holder.getItemViewType() == TYPE_RECEIVE_GROUP) {
            assert holder instanceof GroupChatModelViewHolder;
            GroupChatModelViewHolder viewHolder = (GroupChatModelViewHolder) holder;
            FirebaseUtils.getOtherProfilePicStorageRef(user.getUserId()).getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        AndroidUtils.setProfilePic(context,uri,viewHolder.profile_pic_image_view);
                    });
            viewHolder.leftChatLayout.setVisibility(View.VISIBLE);
            viewHolder.leftChatTextview.setText(model.getMessage());
        }

    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder {

        LinearLayout rightChatLayout;
        TextView rightChatTextview;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);

            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
        }
    }

    class GroupChatModelViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftChatLayout;
        TextView leftChatTextview;
        ImageView profile_pic_image_view;

        public GroupChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_pic_image_view= itemView.findViewById(R.id.profile_pic_image_view);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
        }
    }

    class ReplyPostChatModelViewHolder extends RecyclerView.ViewHolder {
        LinearLayout left_chat_layout;
        TextView leftChatTextview, dateTextView, nameTextView, statusTextView;
        ImageView imagePost, profile_pic_image_view;

        public ReplyPostChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_pic_image_view = itemView.findViewById(R.id.profile_pic_image_view);
            left_chat_layout = itemView.findViewById(R.id.left_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            imagePost = itemView.findViewById(R.id.imagePost);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            statusTextView = itemView.findViewById(R.id.statusText);

        }
    }
}