package com.example.trimob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
    FirebaseAuth mAuth;
    ImageView imageView;
    boolean animationStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        mAuth = FirebaseAuth.getInstance();
        imageView = findViewById(R.id.images);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        imageView.animate()
                .scaleX(0.7f)
                .scaleY(0.7f)
                .setDuration(1500)
                .start();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("datauser").child(uid);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String phone;
                    if (dataSnapshot.exists()) {
                        startAnimationLoop();
                    } else {
                        phone = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhoneNumber();
                        startAnimationLoopPhone(phone);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                    navigateToCollectInfoPhone(null);
                }
            });
        } else {
            startAnimationLoopNav();
        }
    }


    private void startAnimationLoop() {
        if (!animationStarted) {
                        imageView.animate()
                                .scaleX(300.0f)
                                .scaleY(300.0f)
                                .setDuration(1500)
                                .withEndAction(this::navigateToHomePage)
                                .start();
                    }
            animationStarted = true;

    }
    private void startAnimationLoopPhone(String phone) {
        if (!animationStarted) {
                        imageView.animate()
                                .scaleX(300.0f)
                                .scaleY(300.0f)
                                .setDuration(1500)
                                .withEndAction(() -> navigateToCollectInfoPhone(phone))
                                .start();
                    }
            animationStarted = true;

    }
    private void startAnimationLoopNav() {
        if (!animationStarted) {
            imageView.animate()
                    .scaleX(0.7f)
                    .scaleY(0.7f)
                    .setDuration(1500)
                            .withEndAction(() -> {imageView.animate()
                                    .scaleX(300.0f)
                                    .scaleY(300.0f)
                                    .setDuration(1500)
                                    .withEndAction(this::navigateToLoginActivity)
                                    .start();})
            .start();
                    }
            animationStarted = true;

    }

    private void navigateToHomePage() {
        // Stop the animation loop before navigating
        imageView.clearAnimation();

        Intent intent = new Intent(getApplicationContext(), homePage.class);
        startActivity(intent);
        finish();
    }

    private void navigateToCollectInfoPhone(String phone) {
        // Stop the animation loop before navigating
        imageView.clearAnimation();

        Intent intent = new Intent(getApplicationContext(), collectInfoPhone.class);
        if (phone != null) {
            intent.putExtra("phone", phone);
        }
        startActivity(intent);
        finish();
    }

    private void navigateToLoginActivity() {
        // Stop the animation loop before navigating
        imageView.clearAnimation();

        Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
        startActivity(intent);
        finish();
    }
}
