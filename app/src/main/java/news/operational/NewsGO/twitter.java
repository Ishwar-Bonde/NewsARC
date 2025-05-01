package news.operational.NewsGO;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.OAuthProvider;

public class twitter extends LoginActivity {
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();


        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");

        provider.addCustomParameter("lang", "fr");

        Task<AuthResult> pendingResultTask = firebaseAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    startActivity(new Intent(twitter.this, homePage.class));
                                    Toast.makeText(twitter.this, "Login Success", Toast.LENGTH_SHORT).show();
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(twitter.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
        } else {

            firebaseAuth
                    .startActivityForSignInWithProvider(twitter.this, provider.build())
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    startActivity(new Intent(twitter.this, homePage.class));
                                    Toast.makeText(twitter.this, "Login Success", Toast.LENGTH_SHORT).show();
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(twitter.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
        }
    }
}