package com.example.myapplication.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.utils.AndroidUtils;
import com.example.myapplication.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReactionDialog extends DialogFragment implements View.OnClickListener {
    View view;
    ImageView likeReact, loveReact, loveloveReact, hahaReact, wowReact, sadReact, angryReact;
    String currentID, Reaction, userID;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;

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

        userID = FirebaseUtils.currentUserID();
        documentReference = db.collection("posts").document(currentID);

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
            Reaction = "like";
            AndroidUtils.showToast(getContext(),"check"+currentID);
            getDialog().dismiss();
        } else if (viewId == R.id.img_love) {
            Reaction = "love";
            getDialog().dismiss();
        } else if (viewId == R.id.img_lovelove) {
            Reaction = "lovelove";
            getDialog().dismiss();
        } else if (viewId == R.id.img_wow) {
            Reaction = "wow";
            getDialog().dismiss();
        } else if (viewId == R.id.img_haha) {
            Reaction = "haha";
            getDialog().dismiss();
        } else if (viewId == R.id.img_sad) {
            Reaction = "sad";
            getDialog().dismiss();
        } else if (viewId == R.id.img_angry) {
            Reaction = "angry";
            getDialog().dismiss();
        }
        saveReaction();
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
        currentID = args.get("currentPostID").toString();
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
    private void saveReaction() {
        // Kiểm tra xem có document nào trong collection "reactions" chứa userID của người dùng không
        documentReference.collection("reactions")
                .whereEqualTo("userId", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                // Nếu đã có document chứa userID của người dùng
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    // Cập nhật biểu cảm mới vào document đã tồn tại
                                    document.getReference().update("reaction", Reaction)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Cập nhật thành công
                                                    Log.d("TAG", "Reaction updated successfully");
                                                    dismiss();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Xảy ra lỗi khi cập nhật
                                                    Log.w("TAG", "Error updating reaction", e);
                                                    Toast.makeText(requireContext(), "Error updating reaction", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } else {
                                // Nếu không có document chứa userID của người dùng
                                // Thêm một document mới chứa userID và biểu cảm vào collection "reactions"
                                Map<String, Object> reactionData = new HashMap<>();
                                reactionData.put("userId", userID);
                                reactionData.put("reaction", Reaction);
                                documentReference.collection("reactions")
                                        .add(reactionData)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                // Thêm thành công
                                                Log.d("TAG", "Reaction added successfully");
                                                dismiss();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Xảy ra lỗi khi thêm
                                                Log.w("TAG", "Error adding reaction", e);
                                                Toast.makeText(requireContext(), "Error adding reaction", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            // Xảy ra lỗi khi truy vấn
                            Log.w("TAG", "Error getting documents", task.getException());
                            Toast.makeText(requireContext(), "Error getting documents", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}


