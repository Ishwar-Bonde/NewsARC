package com.example.trimob;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class signupPage extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +
                    "(?=.*[a-z])" +
                    "(?=.*[A-Z])" +
                    "(?=.*[@#$%^&+=])" +
                    "(?=\\S+$)" +
                    ".{6,}" +
                    "$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9]{10}$");
    TextInputLayout fullname_var, username_var, email_var, phonenumber_var, password_var;
    ProgressBar progressBar;
    Button buttonReg;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("datauser");
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), homePage.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        progressBar = findViewById(R.id.progressbar_send_otp);

        fullname_var = findViewById(R.id.fullname_field);
        username_var = findViewById(R.id.username_fields);
        email_var = findViewById(R.id.email_field);
        phonenumber_var = findViewById(R.id.phone_number_field);
        password_var = findViewById(R.id.password_field);

        buttonReg = findViewById(R.id.btn_reg);


        Button login_button = findViewById(R.id.btn_log);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname_ = fullname_var.getEditText().getText().toString();
                String username_ = username_var.getEditText().getText().toString();
                String email_ = email_var.getEditText().getText().toString();
                String phonenumber_ = phonenumber_var.getEditText().getText().toString();
                String password_ = password_var.getEditText().getText().toString();

                if (!fullname_.isEmpty() && !username_.isEmpty() && !email_.isEmpty() && !phonenumber_.isEmpty() && !password_.isEmpty()) {
                    // Check if all fields are available
                    if(Patterns.EMAIL_ADDRESS.matcher(email_).matches()){
                        if(PASSWORD_PATTERN.matcher(password_).matches()){
                            checkAllFieldsAvailability(fullname_, username_, email_, phonenumber_, password_);
                        }
                        else{
                            email_var.setErrorEnabled(false);
                            password_var.setError("Password must be at least 8 characters long, include at least one uppercase letter, one number, and have at least one special symbol.");
                        }
                    }else{
                        password_var.setErrorEnabled(false);
                        email_var.setError("Invalid Email");
                    }
                }
                else {
                    Toast.makeText(signupPage.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void checkAllFieldsAvailability(String fullname, String username, String email, String phonenumber, String password) {
        reference = firebaseDatabase.getReference("datauser");

        // Check if the username is already taken
        reference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Username already taken
                    username_var.setError("Username already taken");
                } else {
                    // Check if the email is already taken
                    username_var.setErrorEnabled(false);
                    reference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // Email already taken
                                email_var.setError("Email already exists");
                            } else {
                                email_var.setErrorEnabled(false);
                                reference.orderByChild("phone").equalTo(phonenumber).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            phonenumber_var.setError("Phone number already taken");
                                        } else {
                                            phonenumber_var.setErrorEnabled(false);
                                            password_var.setErrorEnabled(false);
                                            if (PHONE_PATTERN.matcher(phonenumber).matches()) {
                                                progressBar.setVisibility(View.VISIBLE);
                                                buttonReg.setVisibility(View.INVISIBLE);
                                                phonenumber_var.setErrorEnabled(false);
                                                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                                        "+91" + phonenumber, 60, TimeUnit.SECONDS, signupPage.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                                            @Override
                                                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                                                progressBar.setVisibility(View.GONE);
                                                                buttonReg.setVisibility(View.VISIBLE);
                                                            }

                                                            @Override
                                                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                                                progressBar.setVisibility(View.GONE);
                                                                buttonReg.setVisibility(View.VISIBLE);
                                                                Toast.makeText(signupPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }

                                                            @Override
                                                            public void onCodeSent(@NonNull String backendotp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                                                progressBar.setVisibility(View.GONE);
                                                                buttonReg.setVisibility(View.VISIBLE);

                                                                Toast.makeText(signupPage.this, "Otp Sent", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(getApplicationContext(), VerifyEnterOtp.class);
                                                                intent.putExtra("backendotp", backendotp);
                                                                intent.putExtra("mobile", phonenumber);
                                                                intent.putExtra("name", fullname);
                                                                intent.putExtra("email", email);
                                                                intent.putExtra("username", username);
                                                                intent.putExtra("password", password);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        }
                                                );
                                            }else{
                                                phonenumber_var.setError("Invalid phone number");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Handle errors
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle errors
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }
}