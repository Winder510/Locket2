package com.example.myapplication.BottomSheetDialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.myapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BottomSheetOptions extends BottomSheetDialogFragment {
    Button saveBtn, deleteBtn;
    public BottomSheetOptions() {
    }
    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.options_layout,container, false);
        initView(view);
        return view;
    }
    public void initView(View view){
        saveBtn = view.findViewById(R.id.saveBtn);
        deleteBtn = view.findViewById(R.id.deleteBtn);
    }
}
