package news.operational.NewsGO;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import news.operational.NewsGO.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class profilePage extends AppCompatActivity {
    TextView fullname, username, email, name, user_name;
    SwipeRefreshLayout refreshLayout;
    Button edit_profile;
    ImageView backButton;
    String full_name, username1, phone1, email1;
    CircleImageView profile_pic, pic_profile;
    Uri selectedImage;
    FirebaseAuth mAuth;
    ProgressDialog dialog;
    ProgressDialog uploadProgressDialog;
    FirebaseStorage storage;
    FirebaseDatabase database;
    DatabaseReference reference;
    String userID_Co = FirebaseAuth.getInstance().getUid();

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        fullname = findViewById(R.id.profile_name);
        username = findViewById(R.id.user_name);
        user_name = findViewById(R.id.username_p_field);
        name = findViewById(R.id.fullname_field_p);
        email = findViewById(R.id.email_field_p);
        pic_profile = findViewById(R.id.profile_image);
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        refreshLayout = findViewById(R.id.profile_refresh);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("datauser");
                String userID = FirebaseAuth.getInstance().getUid();
                databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                            String authenticationType = snapshot.child("authenticationType").getValue(String.class);
                            if ("Google".equals(authenticationType)) {
                                gettingDataGoogle();
                            } else if ("Phone".equals(authenticationType)) {
                                gettingData();
                            } else {
                                Toast.makeText(profilePage.this, "Unknown authentication type", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        dialog = new ProgressDialog(this);
        dialog.setMessage("Updating profile...");
        dialog.setCancelable(false);

        // Initialize ProgressDialog for uploading photo
        uploadProgressDialog = new ProgressDialog(this);
        uploadProgressDialog.setMessage("Uploading profile photo...");
        uploadProgressDialog.setCancelable(false);

        edit_profile = findViewById(R.id.edit_profile);
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(profilePage.this);
                View dialogView = LayoutInflater.from(profilePage.this).inflate(R.layout.activity_edit_profile, null);
                builder.setView(dialogView);

                ImageView backbtn = dialogView.findViewById(R.id.backbtn);
                profile_pic = dialogView.findViewById(R.id.add_profile_pic);
                TextInputEditText editName = dialogView.findViewById(R.id.edit_name);
                TextInputEditText editUsername = dialogView.findViewById(R.id.edit_username);
                Button saveButton = dialogView.findViewById(R.id.saveButton);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                updateProfileFetch(editName, editUsername, saveButton);

                profile_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, 1);

                    }
                });

                backbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("datauser");
        String userID = FirebaseAuth.getInstance().getUid();
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    String authenticationType = snapshot.child("authenticationType").getValue(String.class);
                    if ("Google".equals(authenticationType)) {
                        gettingDataGoogle();
                    } else if ("Phone".equals(authenticationType)) {
                        gettingData();
                    } else {
                        Toast.makeText(profilePage.this, "Unknown authentication type", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (data.getData() != null) {
                Uri uri = data.getData(); // filepath

                // Show progress dialog when profile photo is being uploaded
                uploadProgressDialog.show();

                FirebaseStorage storage = FirebaseStorage.getInstance();
                long time = new Date().getTime();
                StorageReference reference = storage.getReference().child("Profiles").child(time + "");
                reference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String filePath = uri.toString();
                                    HashMap<String, Object> obj = new HashMap<>();
                                    obj.put("image", filePath);

                                    if (!isFinishing() && uploadProgressDialog.isShowing()) {
                                        uploadProgressDialog.dismiss();
                                    }
                                }
                            });
                        }
                    }
                });
                profile_pic.setImageURI(data.getData());
                selectedImage = data.getData();
            }
        }
    }


    private void gettingData() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("datauser");
        String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    HelperClass helperClass = snapshot.getValue(HelperClass.class);

                    assert helperClass != null;
                    if(helperClass.getImageUrl()!= null){
                        String img = helperClass.getImageUrl();
                        Picasso.get().load(img).into(pic_profile);
                    }
                    fullname.setText(helperClass.getName());
                    username.setText(helperClass.getUsername());
                    user_name.setText(helperClass.getUsername());
                    name.setText(helperClass.getName());
                    email.setText(helperClass.getEmail());
                    progressBar.setVisibility(View.GONE);
                    refreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    private void gettingDataGoogle() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.bringToFront();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("datauser");
        String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    HelperClassGoogle helperClass = snapshot.getValue(HelperClassGoogle.class);

                    assert helperClass != null;
                    if(helperClass.getImageURL()!= null){
                        String img = helperClass.getImageURL();
                        Picasso.get().load(img).into(pic_profile);
                    }
                    fullname.setText(helperClass.getName());
                    username.setText(helperClass.getUsername());
                    user_name.setText(helperClass.getUsername());
                    name.setText(helperClass.getName());
                    email.setText(helperClass.getEmail());
                    progressBar.setVisibility(View.GONE);
                    refreshLayout.setRefreshing(false);
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
        String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    HelperClass helperClass = snapshot.getValue(HelperClass.class);

                    assert helperClass != null;
                    if(helperClass.getImageUrl()!= null){
                        String imgUrl = helperClass.getImageUrl();
                        Picasso.get().load(imgUrl).into(profile_pic);
                    }
                    editName.setText(helperClass.getName());
                    editUsername.setText(helperClass.getUsername());
                    full_name = helperClass.getName();
                    username1 = helperClass.getUsername();
                    email1 = helperClass.getEmail();

                    String authenticationType = helperClass.getAuthenticationType();

                    if ("Google".equals(authenticationType) || "Phone".equals(authenticationType)) {
                        saveButton.setEnabled(true);

                        saveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if ("Google".equals(authenticationType)) {
                                    updateProfileGoogle(editName, editUsername);
                                    uploadImgGoogle(authenticationType);
                                } else {
                                    updateProfilePhone(editName, editUsername);
                                    uploadImgPhone(authenticationType);
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

    private void uploadImgGoogle(String authenticationType) {
        // Show progress dialog when profile photo is being uploaded
        dialog.show();

        String userID = FirebaseAuth.getInstance().getUid();
        if (selectedImage != null) {
            assert userID != null;
            StorageReference reference = storage.getReference().child("Profiles").child(userID);
            reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String imageUrl = uri.toString();
                                HelperClassGoogle addNewUser = new HelperClassGoogle(username1, full_name, email1, phone1, authenticationType, imageUrl);

                                database.getReference()
                                        .child("datauser")
                                        .child(userID)  // Use UID instead of phone
                                        .setValue(addNewUser)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Dismiss the progress dialog when upload is successful
                                                dialog.dismiss();
                                            }
                                        });

                            }
                        });
                    }
                }
            });
        }

    }

    private void uploadImgPhone(String authenticationType) {
        // Show progress dialog when profile photo is being uploaded
        dialog.show();

        if (selectedImage != null) {
            StorageReference reference = storage.getReference().child("Profiles").child(userID_Co);
            reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String imageUrl = uri.toString();
                                HelperClass addNewUser = new HelperClass(full_name, email1, username1, phone1,authenticationType, imageUrl);

                                assert userID_Co != null;
                                database.getReference()
                                        .child("datauser")
                                        .child(userID_Co)
                                        .setValue(addNewUser)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Dismiss the progress dialog when upload is successful
                                                dialog.dismiss();
                                            }
                                        });
                            }
                        });
                    }
                }
            });
        }
    }

    private void updateProfileGoogle(TextInputEditText editName, TextInputEditText editUsername) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("datauser");

        String userID = FirebaseAuth.getInstance().getUid();
        assert userID != null;
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HelperClassGoogle helperClassGoogle = snapshot.getValue(HelperClassGoogle.class);

                if (helperClassGoogle != null) {
                    String updatePhone = helperClassGoogle.getPhone();
                    String updateImg = helperClassGoogle.getImageURL();
                    String updateName = editName.getText().toString();
                    String updateUsername = editUsername.getText().toString();
                    String authenticationType = "Google";
                    String updateEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

                    HelperClassGoogle updateClass = new HelperClassGoogle(updateUsername, updateName, updateEmail, updatePhone, authenticationType,updateImg);

                    databaseReference.child(userID).setValue(updateClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(updateName).build();
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                assert firebaseUser != null;
                                firebaseUser.updateProfile(profileUpdates);
                                Toast.makeText(profilePage.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
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
                    String updateEmail = helperClass.getEmail();
                    String imageURL = helperClass.getImageUrl();

                    HelperClass updateClass = new HelperClass(updateName, updateEmail, updateUsername, updatePhone, authenticationType,imageURL);

                    databaseReference.child(userID).setValue(updateClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(updateName).build();
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                assert firebaseUser != null;
                                firebaseUser.updateProfile(profileUpdates);
                                Toast.makeText(profilePage.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(profilePage.this, "Phone number not available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (uploadProgressDialog != null && uploadProgressDialog.isShowing()) {
            uploadProgressDialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

}
