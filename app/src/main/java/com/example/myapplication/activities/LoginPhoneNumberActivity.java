package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.hbb20.CountryCodePicker;

public class LoginPhoneNumberActivity extends AppCompatActivity {
    CountryCodePicker countryCodePicker;
    EditText phoneInput;
    Button sendOtpBtn;
    ProgressBar progressBar;

    @Override   
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);

        countryCodePicker = findViewById(R.id.login_countrycode);
        phoneInput = findViewById(R.id.login_mobile_number);
        sendOtpBtn = findViewById(R.id.send_otp_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        progressBar.setVisibility(View.GONE);
        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        sendOtpBtn.setOnClickListener((v)->{
            if(!countryCodePicker.isValidFullNumber()){
                phoneInput.setError("Số điện thoại không hợp lệ ");
                return;
            }
            Intent intent = new Intent(LoginPhoneNumberActivity.this,LoginOTPActivity.class);
            intent.putExtra("phone", countryCodePicker.getFullNumberWithPlus());
            phoneInput.setText("0"+phoneInput.getText().toString());
            String phoneInputString = phoneInput.getText().toString();
            phoneInputString = phoneInputString.replaceAll("\\s+", "");
            phoneInput.setText(phoneInputString);
            intent.putExtra("phonesearch",phoneInput.getText().toString());
            startActivity(intent);
        });

    }

}
