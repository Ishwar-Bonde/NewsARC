package news.operational.NewsGO.Admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import news.operational.NewsGO.HelperClass;
import news.operational.NewsGO.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendNotification extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription, editTextImageUrl, editTextFcmToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextImageUrl = findViewById(R.id.editTextImageUrl);
        editTextFcmToken = findViewById(R.id.editTextFcmToken);
        Button buttonSendNotification = findViewById(R.id.buttonSendNotification);

        buttonSendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString().trim();
                String description = editTextDescription.getText().toString().trim();
                String imageUrl = editTextImageUrl.getText().toString().trim();
                String fcmToken = editTextFcmToken.getText().toString().trim();

                if (title.isEmpty() || description.isEmpty()) {
                    Toast.makeText(SendNotification.this, "Title and Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                sendNotification(title, description, imageUrl, fcmToken);
            }
        });
    }

    private void sendNotification(String title, String description, String imageUrl, String username) {
        if (username == null || username.isEmpty()) {
            // If username is null or empty, send the notification to all users
            sendNotificationToAllUsers(title, description, imageUrl);
        } else {
            sendNotificationToUser(title, description, imageUrl, username);
        }
    }

    // Method to send notification to all users
    private void sendNotificationToAllUsers(String title, String description, String imageUrl) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("datauser");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        HelperClass helperClass = userSnapshot.getValue(HelperClass.class);
                        if (helperClass != null && helperClass.getFcmToken() != null) {
                            sendSingleNotification(title, description, imageUrl, helperClass.getFcmToken());
                        }
                    }
                } else {
                    // No users found in the database
                    Toast.makeText(SendNotification.this, "No users found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event
            }
        });
    }

    // Method to send notification to a specific user
    private void sendNotificationToUser(String title, String description, String imageUrl, String username) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("datauser");
        usersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        HelperClass helperClass = userSnapshot.getValue(HelperClass.class);
                        if (helperClass != null && helperClass.getFcmToken() != null) {
                            sendSingleNotification(title, description, imageUrl, helperClass.getFcmToken());
                        }
                    }
                } else {
                    // Username not found in the database
                    Toast.makeText(SendNotification.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event
            }
        });
    }

    // Method to send a single notification
    private void sendSingleNotification(String title, String description, String imageUrl, String fcmToken) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", title);
            notificationObj.put("body", description);
            if (!imageUrl.isEmpty()) {
                notificationObj.put("image", imageUrl);
            }

            JSONObject dataObj = new JSONObject();
            // If you need to include any additional data, add it here
            // dataObj.put("key", "value");

            jsonObject.put("notification", notificationObj);
            jsonObject.put("data", dataObj);
            jsonObject.put("to", fcmToken);

            callApi(jsonObject);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    void callApi(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        Request request = new Request.Builder().url(url).post(body)
                .header("Authorization", "Bearer AAAAAxvtdJg:APA91bFwHI4TK2Cp4DSE4v9gwZntoozKD_VRzDDYF3_xqLWiMHDK4tVpIPOWrq464Gc7ytH3ol-EyKypMYFyIfKItDUxsNJmZtyANPW5TPLwcI55WZTyH5fxlRsD32tD-_dZLv1tFINE")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });


    }
}