package com.example.myapplication.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.SearchUserRecyclerAdapter;
import com.example.myapplication.models.User;
import com.example.myapplication.utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchUserActivity extends AppCompatActivity {
    EditText searchInput;
    ImageButton backButton;
    RecyclerView recyclerView;
    SearchUserRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchInput = findViewById(R.id.seach_username_input);

      //  backButton = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.search_user_recycler_view);

        searchInput.requestFocus();
//        backButton.setOnClickListener(v -> {
//            onBackPressed();
//        });
//
//        searchButton.setOnClickListener(v -> {
//            String searchTerm = searchInput.getText().toString();
//            setupSearchRecyclerView(searchTerm);
//        });
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchTerm = searchInput.getText().toString();
                if (searchTerm.isEmpty()) {
                    // Nếu searchTerm rỗng, xóa danh sách
                    clearRecyclerView();
                } else {
                    setupSearchRecyclerView(searchTerm);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    void clearRecyclerView() {
        // Xóa dữ liệu trong RecyclerView và cập nhật UI
        if (adapter != null) {
            adapter.stopListening();
            recyclerView.setAdapter(null);
        }
    }
    void setupSearchRecyclerView(String searchTerm) {
        Query query = FirebaseUtils.allUserCollectionReference().whereGreaterThanOrEqualTo("username", searchTerm)
                .whereLessThanOrEqualTo("username", searchTerm + '\uf8ff');

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>().setQuery(query, User.class).build();
        adapter = new SearchUserRecyclerAdapter(options, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.startListening();
        }
    }
}
