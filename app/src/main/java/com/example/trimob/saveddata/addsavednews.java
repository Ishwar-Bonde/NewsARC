package com.example.trimob.saveddata;

import android.content.Context;
import android.widget.Toast;

import androidx.room.Room;

import java.util.List;

public class addsavednews {
    static saveddatabase db;
    static SavedNewsDao savesNewsOperation;
    Context context;

    public addsavednews(Context context) {
        this.context = context;
        addsavednews.db = Room.databaseBuilder(context, saveddatabase.class, "SavednewsDB").allowMainThreadQueries().build();
        addsavednews.savesNewsOperation = addsavednews.db.savedNewsOperations();
    }

    public void insertingnews(savednews s) {
        addsavednews.savesNewsOperation.insertsavednews(s);
        Toast.makeText(context, "News Saved", Toast.LENGTH_SHORT).show();
    }

    public List<savednews> getAllSavedNews() {
        return addsavednews.savesNewsOperation.getAllSavedNews();
    }


    public int DeleteTheNews(String title) {
        int result = addsavednews.savesNewsOperation.deleteSavedNotes(title);
        return result;
    }
}
