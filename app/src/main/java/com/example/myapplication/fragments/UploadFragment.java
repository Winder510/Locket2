    package com.example.myapplication.fragments;

    import android.Manifest;
    import android.annotation.SuppressLint;
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
    import android.widget.EditText;
    import android.widget.ImageButton;
    import android.widget.ImageView;
    import android.widget.ProgressBar;

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
    import com.example.myapplication.models.Post;
    import com.example.myapplication.models.User;
    import com.example.myapplication.utils.AndroidUtils;
    import com.example.myapplication.utils.FirebaseUtils;
    import com.example.myapplication.utils.ImageUtils;
    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.firebase.Timestamp;
    import com.google.firebase.firestore.DocumentReference;
    import com.google.firebase.firestore.DocumentSnapshot;
    import com.google.firebase.storage.StorageReference;
    import com.google.firebase.storage.UploadTask;

    import java.io.ByteArrayOutputStream;
    import java.io.OutputStream;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Date;
    import java.util.List;
    import java.util.Locale;
    import java.util.Objects;
    import java.util.concurrent.atomic.AtomicInteger;

    public class UploadFragment extends Fragment {

        ImageButton btnClose, btnSave, btnUpPost;
        RecyclerView recyclerView;
        EditText captionText;
        UserRecyclerAdapter adapter;
        ImageView imagePreview;
        ProgressBar progressBar;
        ArrayList<User> userList = new ArrayList<>();
        private static final int REQUEST_CODE = 1;


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
            btnUpPost = view.findViewById(R.id.btUpPost);
            captionText = view.findViewById(R.id.captionText);
            progressBar = view.findViewById(R.id.progressbar);
            Bundle bundle = getArguments();

            if (bundle != null) {
                String imagePath = bundle.getString("imageFilePath");
                userList.addAll((ArrayList<User>) bundle.getSerializable("list"));

                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

                // Kiểm tra và xử lý thông tin xoay của ảnh
                int rotation = ImageUtils.getExifOrientation(imagePath);
                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);

                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                imagePreview.setImageBitmap(rotatedBitmap);
            }
            setInProgress(false);
            setupChatRecyclerView();
            btnUpPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setInProgress(true);
                    handleUpLoadPost();

                }
            });


            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
            btnSave.setOnClickListener(new View.OnClickListener() {
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

        void handleUpLoadPost() {
            StorageReference storageRef = FirebaseUtils.getCurrentPostImageStorageRef();

            BitmapDrawable drawable = (BitmapDrawable) imagePreview.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageName = "image_" + timeStamp + ".jpg";

            StorageReference imageRef = storageRef.child(imageName);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();

            UploadTask uploadTask = imageRef.putBytes(imageData);

            uploadTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        createAndUploadPost(imageUrl,adapter.getIdAllowedFriend());
                    });
                } else {
                    Exception e = task.getException();
                    e.printStackTrace();

                }
            });
        }

        void createAndUploadPost(String imageUrl,ArrayList<String> allowedUser) {

            Post post = new Post();
            post.setPostCaption(captionText.getText().toString());
            post.setUserId(FirebaseUtils.currentUserID());
            post.setCreated_at(Timestamp.now()); // Thời gian hiện tại
            post.setPostImg_url(imageUrl); // Danh sách URL ảnh, ở đây chỉ có một URL
            if(allowedUser.isEmpty()){
                post.setVisibility("public");
            }
            else{
                post.setVisibility("private");
                post.setAllowed_users(allowedUser);
            }

            FirebaseUtils.getPostsCollectionReference()
                    .add(post)
                    .addOnSuccessListener(documentReference -> {
                        // Lấy ID của tài liệu vừa được thêm vào Firestore
                        String idString = documentReference.getId();

                        // Cập nhật ID vào trường của bài đăng
                        post.setPostId(idString);

                        // Tiến hành cập nhật bài đăng đã có ID
                        updatePostWithId(post);
                    })
                    .addOnFailureListener(e -> {
                        AndroidUtils.showToast(getContext(), "failed");
                        setInProgress(false);
                    });


        }

        private void updatePostWithId(Post post) {
            // Đây là nơi bạn có thể cập nhật bài đăng đã có ID vào Firestore hoặc làm bất kỳ điều gì khác cần thiết.
            // Ví dụ:
            FirebaseUtils.getPostsCollectionReference()
                    .document(post.getPostId())
                    .set(post)
                    .addOnSuccessListener(aVoid -> {

                        setInProgress(false);
                    })
                    .addOnFailureListener(e -> {

                        AndroidUtils.showToast(getContext(), "failed to update post with ID");
                        setInProgress(false);
                    });
        }
        void setInProgress(boolean inProgress) {
            if (inProgress) {
                btnClose.setVisibility(View.GONE);
                btnSave.setVisibility(View.GONE);
                btnUpPost.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            } else {
                btnClose.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.VISIBLE);
                btnUpPost.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
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

        @SuppressLint("NotifyDataSetChanged")
        public void setupChatRecyclerView() {
            adapter = new UserRecyclerAdapter(userList, getContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(adapter);
        }

        @Override
        public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
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
