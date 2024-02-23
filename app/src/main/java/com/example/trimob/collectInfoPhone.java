package com.example.trimob;

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
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.regex.Pattern;

public class collectInfoPhone extends AppCompatActivity {

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
    TextInputLayout fullname_var, username_var, email_var;
    ProgressBar progressBar;
    Button buttonReg;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("datauser");
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    public static final String CHANNEL_ID = "Notification";
    public static final int NOTIFICATION_ID = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progressBar = findViewById(R.id.progressbar_send_otp);

        fullname_var = findViewById(R.id.fullname_field);
        username_var = findViewById(R.id.username_fields);
        email_var = findViewById(R.id.email_field);

        buttonReg = findViewById(R.id.btn_reg);

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String fullname_ = fullname_var.getEditText().getText().toString();
                String username_ = username_var.getEditText().getText().toString();
                String email_ = email_var.getEditText().getText().toString();
                String phonenumber_ = getIntent().getStringExtra("phone");

                if (!fullname_.isEmpty() && !username_.isEmpty() && !email_.isEmpty() && !Objects.requireNonNull(phonenumber_).isEmpty()) {
                    if(Patterns.EMAIL_ADDRESS.matcher(email_).matches()) {

                        checkAllFieldsAvailability(fullname_, username_, email_, phonenumber_);
                    }
                    else{
                        email_var.setError("Invalid Email");
                    }
                }
                else {
                    Toast.makeText(collectInfoPhone.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void checkAllFieldsAvailability(String fullname, String username, String email, String phonenumber) {
        reference = firebaseDatabase.getReference("datauser");
        String authenticationType = "Phone";

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
                                email_var.setError("Email already exists");
                            } else {
                                progressBar.setVisibility(View.VISIBLE);
                                buttonReg.setVisibility(View.INVISIBLE);
                                String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("datauser");
                                HelperClass helperClass = new HelperClass(fullname,email,username,phonenumber,authenticationType,null);
                                reference.child(userID).setValue(helperClass);
                                showNotification();
                                Intent intent = new Intent(getApplicationContext(), homePage.class);
                                startActivity(intent);
                                finish();
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
                .setContentText("Success");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        return builder.build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Success");

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