package news.operational.NewsGO;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;

import com.chaos.view.PinView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyEnterOtp extends AppCompatActivity {

    PinView pinView;
    String getOtpBackend;

    PhoneAuthProvider.ForceResendingToken resendingToken;
    ProgressBar progressBar;
    CountDownTimer resendCountDownTimer;
    Long timeoutSeconds = 60L;
    Button verifyotpbtn;
    FirebaseAuth mAuth;
    TextView resendLabel;
    public static final String CHANNEL_ID = "Notification";
    public static final int NOTIFICATION_ID = 100;
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
                "+"+getIntent().getStringExtra("country_code")+"-%s",getIntent().getStringExtra("mobile")
        ));

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
                progressBar.setVisibility(View.GONE);
                verifyotpbtn.setVisibility(View.VISIBLE);
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


                        if (task.isSuccessful()) {
                            String userID = FirebaseAuth.getInstance().getUid();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("datauser");
                            assert userID != null;
                            reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        progressBar.setVisibility(View.GONE);
                                        verifyotpbtn.setVisibility(View.VISIBLE);
                                        showNotification();
                                        Intent intent = new Intent(getApplicationContext(),homePage.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        progressBar.setVisibility(View.GONE);
                                        verifyotpbtn.setVisibility(View.VISIBLE);
                                        Intent intent = new Intent(getApplicationContext(), collectInfoPhone.class);
                                        intent.putExtra("phone", getIntent().getStringExtra("mobile"));
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {
                            Toast.makeText(VerifyEnterOtp.this, "Failed to verify OTP. Please try again.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            verifyotpbtn.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            Toast.makeText(VerifyEnterOtp.this, "Please try again", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            verifyotpbtn.setVisibility(View.VISIBLE);
        }
    }

    private void showNotification() {
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.success, null);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap largeIcon = bitmapDrawable.getBitmap();

        Notification notification = createNotification(largeIcon);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, notification);

        // Dismiss the notification after 2 seconds
        dismissNotificationDelayed(notificationManager);
    }

    private Notification createNotification(Bitmap largeIcon) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.drawable.success)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Success, Welcome Back");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        return builder.build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Success, Welcome Back");

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void dismissNotificationDelayed(NotificationManagerCompat notificationManager) {
        // Delay dismissal by 2 seconds
        final int delayMillis = 2000;
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                notificationManager.cancel(NOTIFICATION_ID);
            }
        }, delayMillis);
    }


}