package com.example.myapplication.BottomSheetDialog;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.myapplication.R;
import com.example.myapplication.utils.AndroidUtils;
import com.example.myapplication.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.OutputStream;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BottomSheetOptions extends BottomSheetDialogFragment {
    Button saveBtn, deleteBtn;
    ImageView imageView;
    String postId, postUrl;

    public BottomSheetOptions() {
    }

    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.options_layout, container, false);
        initView(view);
        return view;
    }

    public void initView(View view) {
        saveBtn = view.findViewById(R.id.saveBtn);
        deleteBtn = view.findViewById(R.id.deleteBtn);

        Bundle args = getArguments();
        postId = args.getString("currentPostID");

        if (postId != null) {
            loadImageFromFirebase(postId);
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    saveImage();
                } else {
                    requestStoragePermission();
                }
            }
        });
    }

    private void loadImageFromFirebase(String postId) {
        FirebaseUtils.getPostsCollectionReference().document(postId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Lấy postUrl từ documentSnapshot
                    postUrl = documentSnapshot.getString("postImg_url");
                } else {
                    // Xử lý khi không tìm thấy postId trong cơ sở dữ liệu
                    AndroidUtils.showToast(requireContext(), "Post not found");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Xử lý khi truy vấn thất bại
                AndroidUtils.showToast(requireContext(), "Failed to fetch post");
            }
        });
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    saveImage();
                } else {
                    AndroidUtils.showToast(requireContext(), "Please provide required permission");
                }
            });

    private void requestStoragePermission() {
        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void saveImage() {
        if (postUrl != null) {
            // Tải ảnh từ URL sử dụng Glide
            Glide.with(requireContext())
                    .asBitmap()
                    .load(postUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            // Khi ảnh đã được tải thành công, tiến hành lưu vào bộ nhớ
                            saveBitmapToGallery(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // Code xử lý khi tải ảnh bị hủy
                        }
                    });
        } else {
            // Xử lý khi postUrl không tồn tại
            AndroidUtils.showToast(requireContext(), "Post image URL is null");
        }
    }

    private void saveBitmapToGallery(Bitmap bitmap) {
        // Tạo Uri cho ảnh
        Uri imageUri;
        ContentResolver contentResolver = requireActivity().getContentResolver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        // Tạo một đối tượng ContentValues để lưu thông tin ảnh
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis() + ".jpg");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        // Insert ảnh vào MediaStore
        Uri uri = contentResolver.insert(imageUri, contentValues);
        if (uri != null) {
            try {
                // Mở OutputStream để ghi dữ liệu vào ảnh
                OutputStream outputStream = contentResolver.openOutputStream(uri);
                if (outputStream != null) {
                    // Nén và ghi ảnh vào OutputStream
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                    // Đóng OutputStream sau khi ghi xong
                    outputStream.close();

                    // Hiển thị thông báo khi lưu ảnh thành công
                    AndroidUtils.showToast(requireContext(), "Image saved successfully");
                    dismiss();
                } else {
                    // Xử lý khi không thể mở OutputStream
                    AndroidUtils.showToast(requireContext(), "Failed to open output stream");
                }
            } catch (Exception e) {
                // Xử lý khi ghi ảnh bị lỗi
            }
        }
    }
}
