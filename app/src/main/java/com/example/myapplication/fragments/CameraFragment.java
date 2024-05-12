package com.example.myapplication.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.myapplication.R;
import com.example.myapplication.activities.RecentChatActivity;
import com.example.myapplication.activities.SearchUserActivity;
import com.example.myapplication.activities.SettingsActivity;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.AndroidUtils;
import com.example.myapplication.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;


import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import maes.tech.intentanim.CustomIntent;

public class CameraFragment extends Fragment{
    ImageButton capture, toggleFlash, flipCamera, btnSetting, btnRecentChat;
    Button btnSearchUser;
    private PreviewView previewView;
    TextView badge;
    // for <setting layout>
    User currentUser;
    Uri uriImage;
    ///>

    //for <uploadFragment>
    ArrayList<User> userList;
    int cameraFacing = CameraSelector.LENS_FACING_BACK;
    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                startCamera(cameraFacing);
            }
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        previewView = view.findViewById(R.id.cameraPreview);
        capture = view.findViewById(R.id.btnCapture);
        toggleFlash = view.findViewById(R.id.btnToggleFlash);
        flipCamera = view.findViewById(R.id.btnFlipCamera);
        btnSetting = view.findViewById(R.id.btnSetting);
        btnSearchUser = view.findViewById(R.id.btnSearchUser);
        btnRecentChat = view.findViewById(R.id.btnRecentChat);
        badge = view.findViewById(R.id.badge);


        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.CAMERA);
        } else {
            startCamera(cameraFacing);
        }



        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClickSettingButton();

            }
        });
        btnRecentChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), RecentChatActivity.class);
                startActivity(intent);
                CustomIntent.customType(requireContext(), "left-to-right");


            }
        });
        btnSearchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), SearchUserActivity.class);
                startActivity(intent);
                CustomIntent.customType(requireContext(), "left-to-right");
            }
        });
        flipCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraFacing == CameraSelector.LENS_FACING_BACK) {
                    cameraFacing = CameraSelector.LENS_FACING_FRONT;
                } else {
                    cameraFacing = CameraSelector.LENS_FACING_BACK;
                }
                startCamera(cameraFacing);
            }
        });


        ExecutorService executorService = Executors.newFixedThreadPool(4);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                // Fetch the data for the badge from the Firestore database
                FirebaseUtils.InviteReference()
                        .whereEqualTo("receiverId", FirebaseUtils.currentUserID())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                final int badgeCount = task.getResult().size();
                                // Update the badge in the UI thread
                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (badgeCount == 0) {
                                            badge.setVisibility(View.GONE);
                                        } else {
                                            badge.setVisibility(View.VISIBLE);
                                            badge.setText(String.valueOf(badgeCount));
                                        }
                                    }
                                });
                            } else {
                                if (task.getException() != null) {
                                    task.getException().printStackTrace();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SearchUserActivity.receiveItemCount == 0) {
            badge.setVisibility(View.GONE);
        } else {
            badge.setVisibility(View.VISIBLE);
            badge.setText(String.valueOf(SearchUserActivity.receiveItemCount));
        }
    }

    private void handleClickSettingButton() {

        Intent intent = new Intent(requireContext(), SettingsActivity.class);


        if (currentUser == null) {
            FirebaseUtils.currentUserDetail().get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    currentUser = task.getResult().toObject(User.class);
                    if (currentUser != null) {
                        AndroidUtils.passUserModelAsIntent(intent, currentUser);
                        startActivity(intent);
                        CustomIntent.customType(requireContext(), "right-to-left");

                    }

                }
            });
        } else {
            AndroidUtils.passUserModelAsIntent(intent, currentUser);
            startActivity(intent);
            CustomIntent.customType(requireContext(), "right-to-left");

        }


    }

    public void startCamera(int cameraFacing) {
        int aspectRatio = aspectRatio(previewView.getWidth(), previewView.getHeight());
        ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(requireContext());

        listenableFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = (ProcessCameraProvider) listenableFuture.get();

                Preview preview = new Preview.Builder().setTargetAspectRatio(aspectRatio).build();

                ImageCapture imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(requireActivity().getWindowManager().getDefaultDisplay().getRotation()).build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(cameraFacing).build();

                cameraProvider.unbindAll();

                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

                capture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        }

                        takePicture(imageCapture);

                    }
                });

                toggleFlash.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setFlashIcon(camera);
                    }
                });

                preview.setSurfaceProvider(previewView.getSurfaceProvider());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(getContext()));
    }

    public void takePicture(ImageCapture imageCapture) {
        final File file = new File(requireActivity().getExternalFilesDir(null), System.currentTimeMillis() + ".jpg");
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
        imageCapture.takePicture(outputFileOptions, Executors.newCachedThreadPool(), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleAfterTakePicture(file);

                    }
                });
                startCamera(cameraFacing);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(requireActivity(), "Failed to save: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                startCamera(cameraFacing);
            }
        });
    }

    private void setFlashIcon(Camera camera) {
        if (camera.getCameraInfo().hasFlashUnit()) {
            if (camera.getCameraInfo().getTorchState().getValue() == 0) {
                camera.getCameraControl().enableTorch(true);
                toggleFlash.setImageResource(R.drawable.ic_flash_on);
            } else {
                camera.getCameraControl().enableTorch(false);
                toggleFlash.setImageResource(R.drawable.ic_flash_off);
            }
        } else {
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    }

    private int aspectRatio(int width, int height) {
        double previewRatio = (double) Math.max(width, height) / Math.min(width, height);
        if (Math.abs(previewRatio - 4.0 / 3.0) <= Math.abs(previewRatio - 16.0 / 9.0)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }
    private void handleAfterTakePicture(File file) {
        if(userList.isEmpty()){
            getListUserForRecyclerView(new OnSuccessListener<ArrayList<User>>() {
                @Override
                public void onSuccess(ArrayList<User> list) {
                    userList.addAll(list);
                    Bundle bundle = new Bundle();
                    bundle.putString("imageFilePath", file.getAbsolutePath());
                    bundle.putSerializable("list", list);
                    UploadFragment fragmentUpload = new UploadFragment();
                    fragmentUpload.setArguments(bundle);
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.main, fragmentUpload)
                            .addToBackStack(null)
                            .commit();
                }
            });

        }
        else{
            Bundle bundle = new Bundle();
            bundle.putString("imageFilePath", file.getAbsolutePath());
            bundle.putSerializable("list", userList);
            UploadFragment fragmentUpload = new UploadFragment();
            fragmentUpload.setArguments(bundle);
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main, fragmentUpload)
                    .addToBackStack(null)
                    .commit();
        }

    }

    public void getListUserForRecyclerView(OnSuccessListener<ArrayList<User>> listener) {
        ArrayList<User> userList = new ArrayList<>();
        FirebaseUtils.currentUserDetail().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot.contains("friends")) {
                    Object friendsObject = documentSnapshot.get("friends");
                    ArrayList<String> friendsList = (ArrayList<String>) friendsObject;

                    if (friendsList != null && !friendsList.isEmpty()) {
                        int friendCount = friendsList.size(); // Get the total number of friends
                        AtomicInteger friendCounter = new AtomicInteger(0);

                        for (String friendId : friendsList) {
                            DocumentReference friendUserRef = FirebaseUtils.getFriendDetail(friendId);
                            friendUserRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot friendDocument) {
                                    if (friendDocument.exists()) {
                                        User friendUser = friendDocument.toObject(User.class);
                                        if (friendUser != null) {
                                            userList.add(friendUser);
                                        }
                                    }
                                    if (friendCounter.incrementAndGet() == friendCount) {
                                        userList.add(0, new User("Tất cả"));
                                        listener.onSuccess(userList);
                                    }
                                }
                            });
                        }
                    } else {
                        AndroidUtils.showToast(getContext(), "No friend found");
                    }
                } else {
                    AndroidUtils.showToast(
                            getContext(), "Document does not exist or does not contain the 'friends' field");
                }
            }
        });
    }

}