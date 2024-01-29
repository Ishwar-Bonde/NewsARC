package com.example.trimob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class VerifyEnterOtp extends AppCompatActivity {

    PinView pinView;
    String getOtpBackend,phonenumber,email,username,password,name;

    PhoneAuthProvider.ForceResendingToken resendingToken;
    ProgressBar progressBar;
    CountDownTimer resendCountDownTimer;
    Long timeoutSeconds = 60L;
    Button verifyotpbtn;
    FirebaseAuth mAuth;
    TextView resendLabel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_enter_otp);
        verifyotpbtn = findViewById(R.id.button_chk);
        pinView = findViewById(R.id.pinview);
        mAuth = FirebaseAuth.getInstance();
        resendLabel = findViewById(R.id.textresendotp);
        TextView textView = findViewById(R.id.textmobileshow);
        textView.setText(String.format(
                "+91-%s",getIntent().getStringExtra("mobile")
        ));

        name = getIntent().getStringExtra("name");
        phonenumber = getIntent().getStringExtra("mobile");
        email = getIntent().getStringExtra("email");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");


        getOtpBackend = getIntent().getStringExtra("backendotp");
        progressBar = findViewById(R.id.progressbar_verify_otp);
        resendCountDownTimer = new CountDownTimer(timeoutSeconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateResendText(millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                enableResendButton();
            }
        };

        resendLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Your existing code for resending OTP
                resendOtp();

                // Start the countdown again
                startResendCountdown();
            }
        });

        verifyotpbtn.setOnClickListener(v -> {
            if (!Objects.requireNonNull(pinView.getText()).toString().trim().isEmpty()) {
                verifyOtp(pinView.getText().toString());
            } else {
                Toast.makeText(VerifyEnterOtp.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
            }
        });
        resendLabel.setOnClickListener(v -> resendOtp());
    }
    private void startResendCountdown() {
        resendCountDownTimer.cancel();
        resendCountDownTimer.start();
        disableResendButton();
    }

    private void updateResendText(long secondsUntilEnable) {
        resendLabel.setText(getString(R.string.resend_otp_disabled, secondsUntilEnable));
    }

    private void enableResendButton() {
        resendLabel.setEnabled(true);
        resendLabel.setText(getString(R.string.resend_otp_enabled));
    }

    private void disableResendButton() {
        resendLabel.setEnabled(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start the countdown when the activity starts
        startResendCountdown();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Cancel the countdown when the activity stops
        resendCountDownTimer.cancel();
    }
    private void resendOtp() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + getIntent().getStringExtra("mobile"), 60, TimeUnit.SECONDS, VerifyEnterOtp.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        // Handle completed verification
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(VerifyEnterOtp.this, "Failed to resend OTP. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String newBackendOtp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        getOtpBackend = newBackendOtp;
                        Toast.makeText(VerifyEnterOtp.this, "Otp Resent", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
    private void verifyOtp(String enteredOTP) {
        String authenticationType = "Phone";
        if (getOtpBackend != null) {
            progressBar.setVisibility(View.VISIBLE);
            verifyotpbtn.setVisibility(View.INVISIBLE);

            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(getOtpBackend, enteredOTP);
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        verifyotpbtn.setVisibility(View.VISIBLE);

                        if (task.isSuccessful()) {

                            String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("datauser");
                            HelperClass helperClass = new HelperClass(name,email,username,password,phonenumber,authenticationType);
                            reference.child(userID).setValue(helperClass);
                            Intent intent = new Intent(getApplicationContext(), homePage.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(VerifyEnterOtp.this, "Failed to verify OTP. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(VerifyEnterOtp.this, "Please check internet connection", Toast.LENGTH_SHORT).show();
        }
    }

}