package com.example.trimob;
import com.example.trimob.saveddata.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class SavedNews extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_news);
        RecyclerView r=findViewById(R.id.savednewsRecyclerView);
        r.setLayoutManager(new LinearLayoutManager(this));
        addsavednews as=new addsavednews(this);

        savedpagecustAdapter CA=new savedpagecustAdapter(as.getAllSavedNews(),this );
        r.setAdapter(CA);
    }
}