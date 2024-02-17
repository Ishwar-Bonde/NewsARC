package com.example.trimob.saveddata;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "savednews")
public class savednews {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "title")
   public String newsTitle;
    @ColumnInfo(name = "newsimage")
 public    String newsImgUrl;
    @ColumnInfo(name = "newsurl")
   public String newsUrl;
    @ColumnInfo(name = "newsdescirption")
   public String newsDesciption;

    public savednews(String newsTitle, String newsImgUrl, String newsUrl, String newsDesciption) {
        this.newsTitle = newsTitle;
        this.newsImgUrl = newsImgUrl;
        this.newsUrl = newsUrl;
        this.newsDesciption = newsDesciption;
    }

    @Override
    public String toString() {
        return "savednews{" +
                "newsTitle='" + newsTitle + '\'' +
                ", newsImgUrl='" + newsImgUrl + '\'' +
                ", newsUrl='" + newsUrl + '\'' +
                ", newsDesciption='" + newsDesciption + '\'' +
                '}';
    }
}
