package news.operational.NewsGO;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;

public class firstPage extends AppCompatActivity {
    private ImageView zoomImageView;
    private static final String SHARED_PREF_NAME = "settings";
    private static final String KEY_NIGHT_MODE = "night_mode";
    private static final String KEY_SYSTEM_THEME = "system_theme_enabled";
    private static final int PERMISSION_REQUEST_CODE = 90;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        boolean isSystemThemeEnabled = sharedPreferences.getBoolean(KEY_SYSTEM_THEME, false);

        if (isSystemThemeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else {
            boolean isNightModeOn = sharedPreferences.getBoolean(KEY_NIGHT_MODE, false);

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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            checkNotificationPermission();
                        } else {
                            startNewActivity();
                        }
                    }
                })
                .start();
    }

    private void checkNotificationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestNotificationPermission();
        } else {
            startNewActivity();
        }
    }

    private void requestNotificationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
    }

    private void startNewActivity() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, homePage.class));
        } else {
            startActivity(new Intent(this, IntroActivity.class));
        }
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startNewActivity();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                startNewActivity(); // You may handle this according to your app logic
            }
        }
    }
}
