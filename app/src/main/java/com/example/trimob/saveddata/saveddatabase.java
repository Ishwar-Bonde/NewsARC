package com.example.trimob.saveddata;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;

import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.RoomDatabase;

import java.util.List;
@Database(entities = savednews.class,version = 1,exportSchema = false)
public abstract class saveddatabase extends RoomDatabase {
 public abstract SavedNewsDao savedNewsOperations();
}

@Dao
interface SavedNewsDao{
    @Insert
    void insertsavednews(savednews n);

    @Query("Delete from savednews where title= :newstitle")
    int deleteSavedNotes(String newstitle);
    @Query("select * from savednews")
    List<savednews> getAllSavedNews();

}






