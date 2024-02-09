package com.example.trimob;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {


    TextInputEditText loginPhone;
    TextInputLayout phone_var;
    CountryCodePicker countryCodePicker;
    ProgressBar pb;
    Button buttonLog;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ImageView google_btn;
    ImageView twitter, facebookbtn;

    CallbackManager callbackManager;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), homePage.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginPhone = findViewById(R.id.login_phone);

        phone_var = findViewById(R.id.username_field);

        buttonLog = findViewById(R.id.btn_login);
        countryCodePicker = findViewById(R.id.country_code);

        pb = findViewById(R.id.progressbar);

        twitter = findViewById(R.id.twitter);
        facebookbtn = findViewById(R.id.facebook_btn);

        buttonLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone_ = Objects.requireNonNull(phone_var.getEditText()).getText().toString();

                if (!phone_.isEmpty()) {
                    // Check if all fields are available
                    pb.setVisibility(View.VISIBLE);
                    buttonLog.setVisibility(View.GONE);
                    String country_code = countryCodePicker.getSelectedCountryCode();
                    Toast.makeText(LoginActivity.this, country_code, Toast.LENGTH_SHORT).show();
                    checkAllFieldsAvailability(phone_,country_code);
                }

            }
        });
        google_btn = findViewById(R.id.google_btn);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.web_client_id)).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        google_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, twitter.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
        facebookbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, Facebook_login.class));
                finish();

            }
        });
    }

    void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1234);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(LoginActivity.this, "Authentication Success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), collectUserInfo.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } catch (ApiException e) {
                Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkAllFieldsAvailability(String phone, String country_code) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+"+country_code + phone, 60, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        pb.setVisibility(View.GONE);
                        buttonLog.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        pb.setVisibility(View.GONE);
                        buttonLog.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String backendotp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        buttonLog.setVisibility(View.VISIBLE);

                        Toast.makeText(LoginActivity.this, "Otp Sent", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), VerifyEnterOtp.class);
                        intent.putExtra("backendotp", backendotp);
                        intent.putExtra("country_code",country_code);
                        intent.putExtra("mobile", phone);
                        startActivity(intent);
                    }
                }
        );
    }


}