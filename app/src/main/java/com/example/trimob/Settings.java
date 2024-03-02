package com.example.trimob;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Locale;
import java.util.Objects;

public class Settings extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    ImageView profile_img;
    TextView profile_name, profile_email;
    MaterialButton button_signout;
    String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    ImageView back_btn;
    RelativeLayout app_lang;
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "settings";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_NEWS_LANGUAGE = "news_language";
    SwitchCompat switchCompat;
    boolean isNightModeOn;
    private static final String KEY_NIGHT_MODE = "night_mode";
    private static final String KEY_SYSTEM_THEME = "system_theme_enabled";
    private boolean languageChanged = false;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        isNightModeOn = sharedPreferences.getBoolean(KEY_NIGHT_MODE, false);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        button_signout = findViewById(R.id.signout_btn);
        assert user != null;
        profile_img = findViewById(R.id.profile_image);
        profile_name = findViewById(R.id.profile_name);
        profile_email = findViewById(R.id.profile_email);
        back_btn = findViewById(R.id.back_btn_settings);
        switchCompat = findViewById(R.id.dark_mode);
        switchCompat.setChecked(isNightModeOn);
        SwitchCompat system_theme = findViewById(R.id.system_theme);
        boolean isSystemThemeEnabled = sharedPreferences.getBoolean(KEY_SYSTEM_THEME, false);
        system_theme.setChecked(true);
        switchCompat.setEnabled(!system_theme.isChecked());
        system_theme.setChecked(isSystemThemeEnabled);
        switchCompat.setEnabled(!isSystemThemeEnabled);
        system_theme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    switchCompat.setEnabled(false);
                } else {
                    AppCompatDelegate.setDefaultNightMode(isNightModeOn ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                    switchCompat.setEnabled(true);
                }
                sharedPreferences.edit().putBoolean(KEY_SYSTEM_THEME, isChecked).apply();
            }
        });

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean(KEY_NIGHT_MODE, isChecked).apply();
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        button_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(Settings.this, "Logout Success", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        gettingData();
        app_lang = findViewById(R.id.app_lang);
        app_lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLanguage();

            }
        });
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        // Load language preference
        String selectedLanguage = sharedPreferences.getString(KEY_LANGUAGE, "");
        if (!selectedLanguage.isEmpty()) {
            setLocale(selectedLanguage);
        }
        RelativeLayout newsLanguageLayout = findViewById(R.id.news_language);
        newsLanguageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguageSelectionDialog();
            }
        });

    }


    private void showLanguageSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose News Language");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_language_selection, null);
        builder.setView(dialogView);

        RadioGroup languageRadioGroup = dialogView.findViewById(R.id.languageRadioGroup);
        RadioButton englishRadioButton = dialogView.findViewById(R.id.englishRadioButton);
        RadioButton hindiRadioButton = dialogView.findViewById(R.id.hindiRadioButton);
        hindiRadioButton.setText("Dutch");

        String currentLanguage = sharedPreferences.getString(KEY_NEWS_LANGUAGE, "");
        if ("nl".equals(currentLanguage)) {
            hindiRadioButton.setChecked(true);
        } else {
            englishRadioButton.setChecked(true);
        }
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedId = languageRadioGroup.getCheckedRadioButtonId();
                if (selectedId == R.id.englishRadioButton) {
                    updateNewsLanguage("en");
                } else if (selectedId == R.id.hindiRadioButton) {
                    updateNewsLanguage("nl");
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateNewsLanguage(String languageCode) {
        sharedPreferences.edit().putString(KEY_NEWS_LANGUAGE, languageCode).apply();
        Toast.makeText(Settings.this, "News language updated to " + languageCode.toUpperCase(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Please restart the app", Toast.LENGTH_SHORT).show();
    }

    private void changeLanguage() {
        final String[] languages = new String[]{"English", "Hindi"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Language");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_language_selection, null);
        builder.setView(dialogView);

        RadioGroup languageRadioGroup = dialogView.findViewById(R.id.languageRadioGroup);
        RadioButton englishRadioButton = dialogView.findViewById(R.id.englishRadioButton);
        RadioButton hindiRadioButton = dialogView.findViewById(R.id.hindiRadioButton);

        String currentLanguage = sharedPreferences.getString(KEY_LANGUAGE, "");
        if ("hi".equals(currentLanguage)) {
            hindiRadioButton.setChecked(true);
        } else {
            englishRadioButton.setChecked(true);
        }
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedId = languageRadioGroup.getCheckedRadioButtonId();
                if (selectedId == R.id.englishRadioButton) {
                    setLocale("en");
                    languageChanged = true;
                    sharedPreferences.edit().putString(KEY_LANGUAGE, "en").apply();
                } else if (selectedId == R.id.hindiRadioButton) {
                    setLocale("hi");
                    languageChanged = true;
                    sharedPreferences.edit().putString(KEY_LANGUAGE, "hi").apply();
                }
                dialog.dismiss();
                recreate();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        Configuration configuration = new Configuration();
        configuration.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

    }

    private void gettingData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("datauser");
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String imgUrl;
                    HelperClass helperClass = snapshot.getValue(HelperClass.class);

                    assert helperClass != null;
                    if (helperClass.getImageUrl() != null) {
                        imgUrl = helperClass.getImageUrl();
                        Picasso.get().load(imgUrl).into(profile_img);
                    }
                    profile_name.setText(helperClass.getName());
                    profile_email.setText(helperClass.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}