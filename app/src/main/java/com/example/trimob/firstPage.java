package com.example.trimob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class firstPage extends AppCompatActivity {
    private ImageView zoomImageView;
    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "settings";
    private static final String KEY_NIGHT_MODE = "night_mode";
    private static final String KEY_SYSTEM_THEME = "system_theme_enabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        boolean isSystemThemeEnabled = sharedPreferences.getBoolean(KEY_SYSTEM_THEME, false);

        if (isSystemThemeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else {
            boolean isNightModeOn = sharedPreferences.getBoolean(KEY_NIGHT_MODE, false);

            // Apply the selected theme
            if (isNightModeOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }

        zoomImageView = findViewById(R.id.images);
        animateZoomOut();

    }
    private void animateZoomOut() {
        zoomImageView.animate()
                .scaleX(0.4f)
                .scaleY(0.4f)
                .setDuration(1000)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        animateZoomIn();
                    }
                })
                .start();
    }

    private void animateZoomIn() {
        zoomImageView.animate()
                .scaleX(500.0f)
                .scaleY(500.0f)
                .setDuration(1000)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        // Start the new activity here
                        startNewActivity();
                    }
                })
                .start();
    }

    private void startNewActivity() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, homePage.class));
        } else {
            startActivity(new Intent(this, NavigationActivity.class));
        }
        finish();
    }


}
