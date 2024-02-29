package com.example.trimob;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        button_signout = findViewById(R.id.signout_btn);
        assert user != null;
        profile_img = findViewById(R.id.profile_image);
        profile_name = findViewById(R.id.profile_name);
        profile_email = findViewById(R.id.profile_email);
        back_btn = findViewById(R.id.back_btn_settings);
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
        hindiRadioButton.setText("Netherland");

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
                    sharedPreferences.edit().putString(KEY_LANGUAGE, "en").apply();
                } else if (selectedId == R.id.hindiRadioButton) {
                    setLocale("hi");
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

//        builder.setSingleChoiceItems(languages,-1,new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // Handle language selection
//                if (which == 0) {
//                    setLocale("en");
//                    recreate();
//                    sharedPreferences.edit().putString(KEY_LANGUAGE, "en").apply();
//                } else {
//                    setLocale("hi");
//                    recreate();
//                    sharedPreferences.edit().putString(KEY_LANGUAGE, "hi").apply();
//                }
//
//            }
//        });
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
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