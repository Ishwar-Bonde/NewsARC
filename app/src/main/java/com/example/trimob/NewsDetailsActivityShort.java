package com.example.trimob;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

public class NewsDetailsActivityShort extends AppCompatActivity {
    String title,desc,content,imageURL,url;
    private TextView titleTV,subDesc,contentTV;
    private ImageView newsIV, backButton;
    private Button readNewsBtn;
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details_short);
        title = getIntent().getStringExtra("title");
        desc = getIntent().getStringExtra("desc");
        content = getIntent().getStringExtra("content");
        imageURL = getIntent().getStringExtra("imageURL");
        url = getIntent().getStringExtra("url");
        titleTV = findViewById(R.id.idTVTitle);
        subDesc = findViewById(R.id.idTVSubDesc);
        contentTV = findViewById(R.id.idTVContent);
        newsIV = findViewById(R.id.idTVNews);
        readNewsBtn = findViewById(R.id.idBtnReadNews);
        backButton = findViewById(R.id.backButtonNewsDetailsShort);
        fab = findViewById(R.id.fab);
        titleTV.setText(title);
        subDesc.setText(desc);
        contentTV.setText(content);
        if(imageURL!=null){
            Picasso.get().load(imageURL).into(newsIV);
        }
        else{
            newsIV.setImageResource(R.drawable.no_image_svg);
        }
        readNewsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),NewsDetailsActivity.class);
                i.putExtra("url",url);
                startActivity(i);
            }
        });
        backButton.setOnClickListener(view -> onBackPressed());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Saved Functionality Not ready on this button", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}