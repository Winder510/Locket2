package com.example.myapplication.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.example.myapplication.activities.OnBackToCameraFragmentListener;
import com.example.myapplication.adapter.ViewPostAdapter;
import com.example.myapplication.models.NestedScrollableHost;
import com.example.myapplication.models.Post;
import com.example.myapplication.utils.AndroidUtils;
import com.example.myapplication.utils.FirebaseUtils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewPostFragment extends Fragment {

    private List<Post> posts;
    private ViewPostAdapter adapter;
    ViewPager2 viewPager2;
    ImageView btnBackToCamera;
    private OnBackToCameraFragmentListener mlistener;

    public ViewPostFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnBackToCameraFragmentListener) {
            mlistener = (OnBackToCameraFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    public static ViewPostFragment newInstance() {
        return new ViewPostFragment();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_post, container, false);
        viewPager2 = rootView.findViewById(R.id.viewpager2);

        adapter = new ViewPostAdapter(posts);
        viewPager2.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        posts = new ArrayList<>();
        loadPosts();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NestedScrollableHost nestedScrollableHost = view.findViewById(R.id.nestedScrollableHost);
        btnBackToCamera = view.findViewById(R.id.btnBackToCamera);
        nestedScrollableHost.setViewPager2(viewPager2);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {

                    nestedScrollableHost.setScrollable(true);
                } else {
                    nestedScrollableHost.setScrollable(false);
                }
            }
        });
        btnBackToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mlistener != null) {
                    viewPager2.setCurrentItem(0);
                    mlistener.onBackToCameraFragment();

                }
            }
        });
    }

    private void loadPosts() {
        AtomicReference<ArrayList<String>> friendsList = new AtomicReference<>();
        FirebaseUtils.currentUserDetail().addSnapshotListener((documentSnapshot, error) -> {
            if (error != null) {
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists() && documentSnapshot.contains("friends")) {
                Object friendsObject = documentSnapshot.get("friends");
                if (friendsObject instanceof ArrayList) {
                  friendsList.set((ArrayList<String>) friendsObject);
                  getPostsOfFriends(friendsList);
                }
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void getPostsOfFriends(AtomicReference<ArrayList<String>> friendsList) {
        ArrayList<String> friends = friendsList.get();
        if (friends == null || friends.isEmpty()) {
            return;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference postsRef = db.collection("posts");
        postsRef.whereIn("userId", friends)
                .whereIn("visibility", Arrays.asList("public", "private"))
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        // Xử lý lỗi nếu có
                        return;
                    }
                    posts.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Post post = document.toObject(Post.class);
//                        if (document.getString("visibility").equals("public") ||
//                                (document.getString("visibility").equals("private") &&
//                                        document.contains("allowed_users") &&
//                                        document.get("allowed_users", Arrays.class).contains(FirebaseUtils.currentUserID()))) {
//                            posts.add(post);
//                        }
                        if(post.getVisibility().equals("public")||
                           post.getVisibility().equals("private")&&post.getAllowed_users().contains(FirebaseUtils.currentUserID())){
                            posts.add(post);
                        }
                    }
                    AndroidUtils.showToast(getContext(), "Check " + queryDocumentSnapshots.size());
                    adapter.notifyDataSetChanged();


                });
    }
}
