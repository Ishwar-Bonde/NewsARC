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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class collectUserInfo extends AppCompatActivity {
    public static final String CHANNEL_ID = "Notification";
    public static final int NOTIFICATION_ID = 100;
    TextInputLayout username, phone;
    Button submit_btn;
    String email;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_user_info);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.success, null);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;

        Bitmap largeIcon = bitmapDrawable.getBitmap();
        Notification notification;

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this)
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Success")
                    .setStyle(new Notification.BigTextStyle().bigText("Success"))
                    .setChannelId(CHANNEL_ID)
                    .build();
            nm.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_HIGH));
        } else {
            notification = new Notification.Builder(this)
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Success")
                    .setStyle(new Notification.BigTextStyle().bigText("Success"))
                    .build();
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            email = currentUser.getEmail();
            name = currentUser.getDisplayName();
        }

        username = findViewById(R.id.username_fields_g);
        phone = findViewById(R.id.phone_number_field_g);
        submit_btn = findViewById(R.id.submit_btn);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the updated username when the user clicks submit
                String username_ = username.getEditText().getText().toString();
                String phone_ = phone.getEditText().getText().toString();
                String authenticationType = "Google";

                if (!username_.isEmpty() || !phone_.isEmpty()) {
                    String profile_img = String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
                    String userID = FirebaseAuth.getInstance().getUid();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("datauser");
                    assert userID != null;
                    reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                showNotification();
                                Intent intent = new Intent(getApplicationContext(), homePage.class);
                                startActivity(intent);
                                finish();
                            } else {
                                HelperClassGoogle helperClassGoogle = new HelperClassGoogle(username_, name, email, phone_, authenticationType, null);

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("datauser");
                                reference.child(userID).setValue(helperClassGoogle)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(collectUserInfo.this, "Success", Toast.LENGTH_SHORT).show();
                                                    showNotification();
                                                    Intent intent = new Intent(getApplicationContext(), homePage.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(collectUserInfo.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    username.setError("Please enter a username");
                }

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