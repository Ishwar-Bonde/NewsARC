package com.example.trimob.saveddata;

import android.content.Context;
import android.widget.Toast;

import androidx.room.Room;

import java.util.List;

public class addsavednews {
  static saveddatabase db;
  Context context;
  static SavedNewsDao savesNewsOperaton;

    public addsavednews(Context context) {
        this.context=context;
        addsavednews.db= Room.databaseBuilder(context,saveddatabase.class,"SavednewsDB").allowMainThreadQueries().build();
        addsavednews.savesNewsOperaton=addsavednews.db.savedNewsOperations();
    }
   public void insertingnews(savednews s){
        addsavednews.savesNewsOperaton.insertsavednews(s);

        Toast.makeText(context, "Saved", Toast.LENGTH_LONG).show();


    }
public List<savednews> getAllSavedNews()
{
    return addsavednews.savesNewsOperaton.getAllSavedNews();
}
}
