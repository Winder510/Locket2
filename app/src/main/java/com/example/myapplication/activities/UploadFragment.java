package com.example.myapplication.activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.UserRecyclerAdapter;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.AndroidUtils;
import com.example.myapplication.utils.FirebaseUtils;
import com.example.myapplication.utils.ImageUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import java.io.OutputStream;
import java.util.Objects;

public class UploadFragment extends Fragment {

    ImageButton btnClose, btnSave;
    RecyclerView recyclerView;
    UserRecyclerAdapter adapter;
    ImageView imagePreview;
    private static final int REQUEST_CODE=1;
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_upload);
//        Intent intent = getIntent();
//        if (intent != null && intent.hasExtra("imagePath")) {
//
//            String imagePath = intent.getStringExtra("imagePath");
//
//             imagePreview = findViewById(R.id.imagePreview);
//            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//
//            // Kiểm tra và xử lý thông tin xoay của ảnh
//            int rotation = ImageUtils.getExifOrientation(imagePath);
//            Matrix matrix = new Matrix();
//            matrix.postRotate(rotation);
//
//            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//            imagePreview.setImageBitmap(rotatedBitmap);
//        }
//        recyclerView = findViewById(R.id.list_user_recycler_view);
//        btnClose = findViewById(R.id.btnClose);
//        btnSave = findViewById(R.id.btnSave);
//
//        btnClose.setOnClickListener(v -> {
//            finish();
//        });
//        btnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(ContextCompat.checkSelfPermission(UploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
//                    saveImage();
//                }
//                else{
//                    ActivityCompat.requestPermissions(UploadActivity.this,new String[]{
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE
//                    },REQUEST_CODE);
//                }
//            }
//        });
//        setupChatRecyclerView();
//
//    }

    void saveImage() {
        Uri images;
        ContentResolver contentResolver = requireActivity().getContentResolver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            images = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis() + ".jpg");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "iamges/*");
        Uri uri = contentResolver.insert(images, contentValues);

        try {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) imagePreview.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            OutputStream outputStream = contentResolver.openOutputStream(Objects.requireNonNull(uri));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            Objects.requireNonNull(outputStream);
            AndroidUtils.showToast(requireContext(), "Image save succesfully");

        } catch (Exception e) {
            AndroidUtils.showToast(requireContext(), "Image not saved ");
            e.printStackTrace();
        }
    }

    public UploadFragment() {
        // Required empty public constructor
    }

    public static UploadFragment newInstance() {
        UploadFragment fragment = new UploadFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imagePreview = view.findViewById(R.id.imagePreview);
        recyclerView = view.findViewById(R.id.list_user_recycler_view);
        btnClose = view.findViewById(R.id.btnClose);
        btnSave = view.findViewById(R.id.btnSave);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String imagePath = bundle.getString("imageFilePath");

            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

            // Kiểm tra và xử lý thông tin xoay của ảnh
            int rotation = ImageUtils.getExifOrientation(imagePath);
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);

            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            imagePreview.setImageBitmap(rotatedBitmap);
        }
        setupChatRecyclerView();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    saveImage();
                }
                else{
                    requestStoragePermission();

                }
            }
        });
    }
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Quyền đã được cấp, thực hiện công việc cần thiết ở đây
                    saveImage();
                } else {
                    // Quyền không được cấp, xử lý tương ứng ở đây (ví dụ: thông báo cho người dùng)
                    AndroidUtils.showToast(requireContext(), "Please provide required permission");
                }
            });

    private void requestStoragePermission() {
        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload, container, false);
    }

    void setupChatRecyclerView() {
        Query query = FirebaseUtils.allUserCollectionReference();
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>().setQuery(query, User.class).build();
        adapter = new UserRecyclerAdapter(options, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImage();
            } else {
                AndroidUtils.showToast(requireContext(), "Please provide required");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
