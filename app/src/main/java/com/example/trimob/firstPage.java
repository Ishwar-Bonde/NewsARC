package com.example.trimob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

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
