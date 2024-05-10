package com.example.myapplication.BottomSheetDialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.UserReactionAdapter;
import com.example.myapplication.models.UserReaction;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BottomSheetReaction extends BottomSheetDialogFragment {
    public BottomSheetReaction() {
    }
    private RecyclerView rcvUserReaction;
    private UserReactionAdapter userReactionAdapter;

    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.reaction_list,container, false);
        initView(view);
        return view;
    }
    private void initView(View view)
    {
        rcvUserReaction = view.findViewById(R.id.rcv_userReaction);
        userReactionAdapter = new UserReactionAdapter(view.getContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(),RecyclerView.VERTICAL, false);
        rcvUserReaction.setLayoutManager(linearLayoutManager);
        userReactionAdapter.setData(getListUserReaction());
        rcvUserReaction.setAdapter(userReactionAdapter);
    }
    private List<UserReaction> getListUserReaction(){
        List<UserReaction> list = new ArrayList<>();
        list.add(new UserReaction(R.drawable.ic_user,"User name 1",R.drawable.ic_haha));
        list.add(new UserReaction(R.drawable.ic_user,"User name 2",R.drawable.ic_haha));
        list.add(new UserReaction(R.drawable.ic_user,"User name 1",R.drawable.ic_haha));
        list.add(new UserReaction(R.drawable.ic_user,"User name 2",R.drawable.ic_haha));
        list.add(new UserReaction(R.drawable.ic_user,"User name 1",R.drawable.ic_haha));
        list.add(new UserReaction(R.drawable.ic_user,"User name 2",R.drawable.ic_haha));
        list.add(new UserReaction(R.drawable.ic_user,"User name 1",R.drawable.ic_haha));
        list.add(new UserReaction(R.drawable.ic_user,"User name 2",R.drawable.ic_haha));
        list.add(new UserReaction(R.drawable.ic_user,"User name 1",R.drawable.ic_haha));
        list.add(new UserReaction(R.drawable.ic_user,"User name 2",R.drawable.ic_haha));
        list.add(new UserReaction(R.drawable.ic_user,"User name 1",R.drawable.ic_haha));
        list.add(new UserReaction(R.drawable.ic_user,"User name 2",R.drawable.ic_haha));
        list.add(new UserReaction(R.drawable.ic_user,"User name 1",R.drawable.ic_haha));
        list.add(new UserReaction(R.drawable.ic_user,"User name 2",R.drawable.ic_haha));



        return list;
    }

}
