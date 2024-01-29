package com.example.trimob;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class profilePage extends AppCompatActivity {
    TextView fullname, username,email,name,phone,user_name;
    ImageView backButton,edit_profile;
    String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        fullname = findViewById(R.id.profile_name);
        username = findViewById(R.id.user_name);
        user_name = findViewById(R.id.username_p_field);
        name = findViewById(R.id.fullname_field_p);
        phone = findViewById(R.id.phone_number_field_p);
        email = findViewById(R.id.email_field_p);

        edit_profile = findViewById(R.id.edit_profile);
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(profilePage.this);
                View dialogView = LayoutInflater.from(profilePage.this).inflate(R.layout.activity_edit_profile, null);
                builder.setView(dialogView);

                ImageView backbtn = dialogView.findViewById(R.id.backbtn);
                TextInputEditText editName = dialogView.findViewById(R.id.edit_name);
                TextInputEditText editUsername = dialogView.findViewById(R.id.edit_username);
                Button saveButton = dialogView.findViewById(R.id.saveButton);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                updateProfileFetch(editName, editUsername, saveButton);

                backbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Dismiss the dialog when back button is clicked
                        alertDialog.dismiss();
                    }
                });

            }
        });
        backButton = findViewById(R.id.back_btn_profile);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        gettingData();
    }

    private void gettingData() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("datauser");
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HelperClass helperClass = snapshot.getValue(HelperClass.class);

                    assert helperClass != null;
                    fullname.setText(helperClass.getName());
                    username.setText(helperClass.getUsername());
                    user_name.setText(helperClass.getUsername());
                    name.setText(helperClass.getName());
                    phone.setText(helperClass.getPhone());
                    email.setText(helperClass.getEmail());
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    private void updateProfileFetch(TextInputEditText editName, TextInputEditText editUsername, Button saveButton) {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("datauser");
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HelperClass helperClass = snapshot.getValue(HelperClass.class);

                    assert helperClass != null;

                    // Set the data into TextInputEditText
                    editName.setText(helperClass.getName());
                    editUsername.setText(helperClass.getUsername());

                    // Fetch the authenticationType
                    String authenticationType = helperClass.getAuthenticationType();

                    // Disable or enable the save button based on authenticationType
                    if ("Google".equals(authenticationType) || "Phone".equals(authenticationType)) {
                        saveButton.setEnabled(true);

                        saveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Check authentication type and call the appropriate update method
                                if ("Google".equals(authenticationType)) {
                                    updateProfileGoogle(editName, editUsername);
                                } else {
                                    updateProfilePhone(editName, editUsername);
                                }
                            }
                        });
                    } else {
                        saveButton.setEnabled(false);
                        Toast.makeText(profilePage.this, "Unknown authentication type", Toast.LENGTH_SHORT).show();
                    }

                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private void updateProfileGoogle(TextInputEditText editName, TextInputEditText editUsername) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("datauser");
        String updateName = editName.getText().toString();
        String updateUsername = editUsername.getText().toString();
        String authenticationType = "Google";
        String updateEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

        String userID = FirebaseAuth.getInstance().getUid();
        assert userID != null;

        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HelperClassGoogle helperClassGoogle = snapshot.getValue(HelperClassGoogle.class);

                if (helperClassGoogle != null) {
                    String updatePhone = helperClassGoogle.getPhone();

                    // Now you have the phone number, you can proceed with the update
                    HelperClassGoogle updateClass = new HelperClassGoogle(updateUsername, updateName, updateEmail, updatePhone, authenticationType);

                    databaseReference.child(userID).setValue(updateClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(updateName).build();
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                assert firebaseUser != null;
                                firebaseUser.updateProfile(profileUpdates);
                                Toast.makeText(profilePage.this, "Update Successful", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            } else {
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (Exception e) {
                                    Toast.makeText(profilePage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                } else {
                    // Handle the case where the phone number is not available
                    Toast.makeText(profilePage.this, "Phone number not available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }

    private void updateProfilePhone(TextInputEditText editName, TextInputEditText editUsername) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("datauser");
        String updateName = editName.getText().toString();
        String updateUsername = editUsername.getText().toString();
        String authenticationType = "Phone";


        String userID = FirebaseAuth.getInstance().getUid();
        assert userID != null;

        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HelperClass helperClass = snapshot.getValue(HelperClass.class);

                if (helperClass != null) {
                    String updatePhone = helperClass.getPhone();
                    String updatePassword = helperClass.getPassword();
                    String updateEmail = helperClass.getEmail();

                    HelperClass updateClass = new HelperClass(updateName, updateEmail, updateUsername, updatePassword, updatePhone, authenticationType);

                    databaseReference.child(userID).setValue(updateClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(updateName).build();
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                assert firebaseUser != null;
                                firebaseUser.updateProfile(profileUpdates);
                                Toast.makeText(profilePage.this, "Update Successful", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            } else {
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (Exception e) {
                                    Toast.makeText(profilePage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                } else {
                    // Handle the case where the phone number is not available
                    Toast.makeText(profilePage.this, "Phone number not available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }

}