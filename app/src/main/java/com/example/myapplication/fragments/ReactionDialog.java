package com.example.myapplication.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.example.myapplication.interfaces.ReactionListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReactionDialog extends DialogFragment implements View.OnClickListener {
    View view;
    ImageView likeReact, loveReact, loveloveReact, hahaReact, wowReact, sadReact, angryReact;

    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialogfragment_reaction, container, false);
        initComponents();
        return view;
    }

    private void initComponents() {
        if (getView() == null) return;
        likeReact = getView().findViewById(R.id.img_like);
        loveReact = getView().findViewById(R.id.img_love);
        loveloveReact = getView().findViewById(R.id.img_lovelove);
        hahaReact = getView().findViewById(R.id.img_haha);
        wowReact = getView().findViewById(R.id.img_wow);
        sadReact = getView().findViewById(R.id.img_sad);
        angryReact = getView().findViewById(R.id.img_angry);

        likeReact.setOnClickListener(this);
        loveReact.setOnClickListener(this);
        loveloveReact.setOnClickListener(this);
        hahaReact.setOnClickListener(this);
        wowReact.setOnClickListener(this);
        sadReact.setOnClickListener(this);
        angryReact.setOnClickListener(this);

    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.img_like) {
            listener.onReactionSelected(0);
            getDialog().dismiss();
        } else if (viewId == R.id.img_love) {
            listener.onReactionSelected(1);
            getDialog().dismiss();
        } else if (viewId == R.id.img_lovelove) {
            listener.onReactionSelected(2);
            getDialog().dismiss();
        } else if (viewId == R.id.img_wow) {
            listener.onReactionSelected(3);
            getDialog().dismiss();
        } else if (viewId == R.id.img_haha) {
            listener.onReactionSelected(4);
            getDialog().dismiss();
        } else if (viewId == R.id.img_sad) {
            listener.onReactionSelected(5);
            getDialog().dismiss();
        } else if (viewId == R.id.img_angry) {
            listener.onReactionSelected(6);
            getDialog().dismiss();
        }
    }

    public void onResume() {
        super.onResume();
        // Set the width of the dialog to match the parent width
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@Nonnull Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        // Lấy tọa độ của nút gọi từ arguments
        Bundle args = getArguments();
        if (args != null && args.containsKey("buttonLocation")) {
            int[] buttonLocation = args.getIntArray("buttonLocation");
            if (buttonLocation != null && buttonLocation.length == 2) {
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.gravity = Gravity.TOP | Gravity.START;
                params.x = buttonLocation[0];
                params.y = buttonLocation[1] - 200;
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                params.dimAmount = 0.0f;
                dialog.getWindow().setAttributes(params);
            }
        }
        // Nền của Dialog được đặt thành trong suốt bằng cách sử dụng màu trong suốt.
        dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

        // Thiết lập cho Dialog có thể huỷ bằng cách chạm bên ngoài của nó.
        dialog.setCanceledOnTouchOutside(true);
    }

    ReactionListener listener;

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ReactionListener) {
            listener = (ReactionListener) activity;
        } else {
            throw new ClassCastException(activity.toString() + " must implement ReactionListener");
        }
    }
}


