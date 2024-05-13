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

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessage, RecyclerView.ViewHolder> {

    private static final int TYPE_SEND = 0;
    private static final int TYPE_RECEIVE_SINGLE = 1;
    private static final int TYPE_RECEIVE_GROUP = 2;
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
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull ChatMessage model) {
        if (holder.getItemViewType() == TYPE_SEND) {
            ChatModelViewHolder viewHolder = (ChatModelViewHolder) holder;
            viewHolder.rightChatLayout.setVisibility(View.VISIBLE);
            viewHolder.rightChatTextview.setText(model.getMessage());
        } else {
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
}