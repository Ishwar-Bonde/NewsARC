package news.operational.NewsGO;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {
    TextInputEditText Name, Username;
    ImageView backbtn,profile_pic;
    Button saveButton;
    String nameUser, usernameUser;
    String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        

        reference = FirebaseDatabase.getInstance().getReference("datauser");

        Name = findViewById(R.id.edit_name);
        Username = findViewById(R.id.edit_username);
        saveButton = findViewById(R.id.saveButton);
        backbtn = findViewById(R.id.backbtn);
        profile_pic = findViewById(R.id.add_profile_pic);
        gettingData();
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditProfileActivity.this, "add", Toast.LENGTH_SHORT).show();
            }
        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNameChanged() || isUsernameChanged()) {
                    Toast.makeText(EditProfileActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfileActivity.this, "No Changes Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean isNameChanged() {
        String newName = Objects.requireNonNull(Name.getText()).toString();
        if (!nameUser.equals(newName)) {
            reference.child(userID).child("name").setValue(newName);
            nameUser = newName;
            return true;
        } else {
            return false;
        }
    }

    public boolean isUsernameChanged() {
        String newUsername = Objects.requireNonNull(Username.getText()).toString();
        if (!usernameUser.equals(newUsername)) {
            reference.child(userID).child("username").setValue(newUsername);
            usernameUser = newUsername;
            return true;
        } else {
            return false;
        }
    }

    private void gettingData() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("datauser");
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HelperClass helperClass = snapshot.getValue(HelperClass.class);

                    assert helperClass != null;
                    Name.setText(helperClass.getName());
                    Username.setText(helperClass.getUsername());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
