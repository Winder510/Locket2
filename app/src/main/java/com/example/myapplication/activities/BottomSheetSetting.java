package com.example.myapplication.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication.R;
import com.example.myapplication.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BottomSheetSetting extends BottomSheetDialogFragment {
    private int layoutResId;
    DocumentReference documentReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public BottomSheetSetting() {
        // Default constructor
    }
    EditText editNameUser;
    Button saveNameBtn;
    public BottomSheetSetting(int layoutResId) {
        this.layoutResId = layoutResId;
    }
    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(layoutResId, container, false);

        saveNameBtn = view.findViewById(R.id.btnSave_Name);

        editNameUser = view.findViewById(R.id.name_edit);

        saveNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = editNameUser.getText().toString().trim();
                if (!newName.isEmpty()) {
                    setUserName(newName);
                } else {
                    Toast.makeText(requireContext(), "Vui lòng nhập tên mới", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Tạo một BottomSheetDialog mới
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        // Lắng nghe sự kiện khi bàn phím xuất hiện và điều chỉnh vị trí của dialog
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return dialog;
    }

    private void setUserName(String newName) {
        if (newName.length() < 3) {
            // Nếu tên mới có ít hơn 3 ký tự, hiển thị thông báo lỗi
            Toast.makeText(requireContext(), "Tên phải có ít nhất 3 ký tự", Toast.LENGTH_SHORT).show();
        } else {
            // Cập nhật giá trị của trường "name" trong tài liệu của người dùng
            documentReference = db.collection("users").document(FirebaseUtils.currentUserID());
            documentReference
                    .update("username", newName)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Cập nhật thành công, hiển thị thông báo
                                Toast.makeText(requireContext(), "Cập nhật tên người dùng thành công", Toast.LENGTH_SHORT).show();
                                dismiss();
                            } else {
                                // Xảy ra lỗi, hiển thị thông báo lỗi
                                Toast.makeText(requireContext(), "Lỗi khi cập nhật tên người dùng", Toast.LENGTH_SHORT).show();
                                // Log lỗi để debug
                                Log.e("FirebaseUtils", "Error updating user name", task.getException());
                            }
                        }
                    });
        }
    }
}
