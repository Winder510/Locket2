package com.example.myapplication.BottomSheetDialog;

import static android.content.ContentValues.TAG;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BottomSheetSetting extends BottomSheetDialogFragment {
    private int layoutResId;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference = db.collection("users").document(FirebaseUtils.currentUserID());
    public BottomSheetSetting() {
        // Default constructor
    }
    EditText editNameUser, editMonthBday, editDateBday;
    Button saveNameBtn, saveBdayBtn;
    public BottomSheetSetting(int layoutResId) {
        this.layoutResId = layoutResId;
    }
    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(layoutResId, container, false);
        handleLayout(view);
        return view;
    }
    private void handleLayout(View view) {
        if (layoutResId == R.layout.edit_name) {
            handleEditNameLayout(view);
        } else if (layoutResId == R.layout.edit_birthday) {
            handleEditBirthdayLayout(view);
        }
    }
    private void handleEditNameLayout(View view) {
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
    }
    private void handleEditBirthdayLayout(View view) {
        saveBdayBtn = view.findViewById(R.id.btnSave_Bday);
        editMonthBday = view.findViewById(R.id.monthBday_edit);
        editDateBday = view.findViewById(R.id.dateBday_edit);

        // Lấy dữ liệu ngày sinh của người dùng từ Firebase Firestore
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Lấy giá trị của trường "birthday" từ tài liệu
                    String userBirthday = documentSnapshot.getString("birthday");

                    // Kiểm tra xem ngày sinh có tồn tại không
                    if (userBirthday != null && !userBirthday.isEmpty()) {
                        String[] parts = userBirthday.split("/");

                        // Kiểm tra xem có đủ phần tử trong mảng không
                        if (parts.length == 2) {
                            // Lấy tháng và ngày từ phần tử tương ứng trong mảng
                            String month = parts[1];
                            String day = parts[0];

                            // Hiển thị tháng và ngày trong EditTexts
                            editMonthBday.setText("tháng " + month);
                            editDateBday.setText("ngày " + day);
                        }
                    }
                } else {
                    Log.d(TAG, "Dữ liệu không tồn tại");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Lỗi khi lấy dữ liệu", e);
            }
        });

        saveBdayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String day = editDateBday.getText().toString().trim();
                String month = editMonthBday.getText().toString().trim();

                // Kiểm tra xem cả hai trường ngày và tháng đều không trống
                if (!day.isEmpty() && !month.isEmpty()) {
                    if (!isValidDateAndMonth(day, month)) {

                    } else {
                        // Tạo chuỗi ngày sinh mới bằng cách kết hợp ngày và tháng
                        String newBirthday = day + "/" + month;
                        setUserBday(newBirthday);
                    }
                } else {
                    // Hiển thị thông báo lỗi nếu người dùng chưa nhập đầy đủ thông tin
                    Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ ngày và tháng sinh", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
    private void setUserBday(String newBirthday) {
            documentReference.update("birthday",newBirthday).
                    addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Cập nhật thành công, hiển thị thông báo
                        Toast.makeText(requireContext(), "Cập nhật sinh nhật người dùng thành công", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        // Xảy ra lỗi, hiển thị thông báo lỗi
                        Toast.makeText(requireContext(), "Lỗi khi cập nhật sinh nhật người dùng", Toast.LENGTH_SHORT).show();
                        // Log lỗi để debug
                        Log.e("FirebaseUtils", "Error updating birthday", task.getException());
                    }
                }
            });
    }
    private boolean isValidDateAndMonth(String day, String month) {
        if (!day.matches("\\d+") || !month.matches("\\d+")) {
            Toast.makeText(requireContext(), "Vui lòng nhập ngày tháng sinh hợp lệ (chỉ chứa số)", Toast.LENGTH_SHORT).show();
            return false;
        }

        int dayValue = Integer.parseInt(day);
        int monthValue = Integer.parseInt(month);

        if (monthValue < 1 || monthValue > 12) {
            Toast.makeText(requireContext(), "Tháng sinh không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }

        switch (monthValue) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                return (dayValue >= 1 && dayValue <= 31);
            case 4: case 6: case 9: case 11:
                return (dayValue >= 1 && dayValue <= 30);
            case 2:
                return (dayValue >= 1 && dayValue <= 28);
        }
        Toast.makeText(requireContext(), "Ngày sinh không hợp lệ", Toast.LENGTH_SHORT).show();
        return false;
    }
}
