package com.example.myapplication.activities;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.utils.AndroidUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LoginOTPActivity extends AppCompatActivity {
    String phoneNumber;
    String phoneSearch;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    Button nextButton;
    EditText otpInput;
    ProgressBar progressBar;
    TextView resendOtpTextView;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Long timeoutSecond = 60L;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);
        nextButton = findViewById(R.id.login_next_btn);
        otpInput = findViewById(R.id.login_otp);
        progressBar = findViewById(R.id.login_progress_bar);
        resendOtpTextView = findViewById(R.id.resend_otp_textview);

        phoneNumber = Objects.requireNonNull(getIntent().getExtras()).getString("phone");
        Intent intent=getIntent();
        phoneSearch=intent.getStringExtra("phonesearch");

        sendOtp(phoneNumber, false);

        nextButton.setOnClickListener(v -> {
            String enterOtp = otpInput.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enterOtp);
            signIn(credential);
        });

        resendOtpTextView.setOnClickListener((v)->{
            sendOtp(phoneNumber,true);
        });
    }

    void sendOtp(String phoneNumber, boolean isResend) {
        startResendTimer();
        setInProgress(true);
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(timeoutSecond, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                String errorMessage = "OTP verification failed";
                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    // Xử lý khi số điện thoại không hợp lệ hoặc lỗi xác thực
                                    errorMessage = "Invalid phone number. Please enter a valid phone number.";
                                } else if (e instanceof FirebaseTooManyRequestsException) {
                                    // Xử lý khi đã vượt quá số lần yêu cầu xác thực
                                    errorMessage = "Quota exceeded. Please try again later.";
                                } else {
                                    // Xử lý các lỗi xác thực khác
                                    errorMessage = e.getMessage(); // Lấy thông điệp lỗi chi tiết
                                }

                                // Hiển thị thông báo lỗi cho người dùng
                                AndroidUtils.showToast(getApplicationContext(), errorMessage);
                                setInProgress(false);
                                // Ghi thông điệp lỗi ra Logcat
                                Log.e("OTP Verification", "Error: " + errorMessage);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                AndroidUtils.showToast(getApplicationContext(), "OTP send successfully");
                                setInProgress(false);
                            }
                        });  // OnVerificationStateChangedCallbacks
        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }

    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
        }
    }

    void signIn(PhoneAuthCredential phoneAuthCredential) {
        // login go to next activity
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginOTPActivity.this, LoginUsernameActivity.class);
                    intent.putExtra("phone", phoneNumber);
                    intent.putExtra("phonesearch",phoneSearch);
                    startActivity(intent);
                } else {
                    AndroidUtils.showToast(getApplicationContext(), "OTP verification failed");
                    setInProgress(false);
                }
            }
        });
    }


    void startResendTimer() {
        resendOtpTextView.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                timeoutSecond--;
                resendOtpTextView.setText("Resend OTP in " + timeoutSecond + "second");
                if(timeoutSecond<=0){
                    timeoutSecond= 60L;
                    timer.cancel();
                    runOnUiThread(() -> {
                        resendOtpTextView.setEnabled(true);
                    });
                }
            }
        }, 0, 1000);
    }
}
