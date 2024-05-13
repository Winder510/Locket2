package com.example.myapplication.BottomSheetDialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.UserReactionAdapter;
import com.example.myapplication.models.UserReaction;
import com.example.myapplication.utils.AndroidUtils;
import com.example.myapplication.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.common.io.Resources;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.rpc.context.AttributeContext;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BottomSheetReaction extends BottomSheetDialogFragment {
    private String currentPostID;
    public BottomSheetReaction() {
    }
    public BottomSheetReaction(String currentPostID) {
        this.currentPostID = currentPostID;
    }
    private RecyclerView rcvUserReaction;
    private UserReactionAdapter userReactionAdapter;
    private String userName;
    private String Reactiontype;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;

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
        rcvUserReaction.setAdapter(userReactionAdapter);
        getListUserReaction();
    }
    private void getListUserReaction() {
        documentReference = db.collection("posts").document(currentPostID);
        documentReference.collection("reactions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final List<UserReaction> list = new ArrayList<>();
                            final int[] count = {0};
                            int totalCount = task.getResult().size();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String userID = document.getString("userId");
                                db.collection("users").document(userID)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> userTask) {
                                                if (userTask.isSuccessful()) {
                                                    DocumentSnapshot userDocument = userTask.getResult();
                                                    if (userDocument.exists()) {
                                                        userName = userDocument.getString("username");
                                                        Reactiontype = document.getString("reaction");
                                                        String drawableName = "ic_" + Reactiontype.toLowerCase();
                                                        int drawableResourceId = getResources().getIdentifier(drawableName, "drawable", getContext().getPackageName());
                                                        list.add(new UserReaction(userID, userName, drawableResourceId));
                                                    }
                                                } else {
                                                    Log.e("getListUserReaction", "Error getting user document", userTask.getException());
                                                }

                                                count[0]++;
                                                if (count[0] == totalCount) {
                                                    // All queries completed, update UI
                                                    userReactionAdapter.setData(list);
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.e("getListUserReaction", "Error getting reactions", task.getException());
                        }
                    }
                });
    }
}
