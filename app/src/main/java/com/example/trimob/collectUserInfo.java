package com.example.trimob;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class collectUserInfo extends AppCompatActivity {
    TextInputLayout username,phone;
    Button submit_btn;
    SharedPreferences sharedPreferences;
    String email;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_user_info);

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
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String profile_img = String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
                    HelperClassGoogle helperClassGoogle = new HelperClassGoogle(username_, name, email, phone_, authenticationType,profile_img);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("datauser");
                    reference.child(userID).setValue(helperClassGoogle)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(collectUserInfo.this, "Success", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), homePage.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(collectUserInfo.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    username.setError("Please enter a username");
                }

            }
        });
    }
    private void redirectToHomePage() {
        Intent intent = new Intent(getApplicationContext(), homePage.class);
        startActivity(intent);
        finish();
    }
}