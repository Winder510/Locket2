package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;

public class LoginWithEmail extends AppCompatActivity {

    Button btnsdt,Passemail;
    EditText emailInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_with_email);
        btnsdt=findViewById(R.id.btnsdt);
        Passemail=findViewById(R.id.mkemail);
        emailInput=findViewById(R.id.login_email);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnsdt.setOnClickListener((v)->{

            Intent intent = new Intent(LoginWithEmail.this, LoginPhoneNumberActivity.class);
            startActivity(intent);
        });
        Passemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginWithEmail.this, LoginEmailPassActivity.class);
                startActivity(intent);
            }
        });

    }
}