package com.example.trimob.saveddata;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "savedNews")
public class savednews {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "title")
    public String newsTitle;
    @ColumnInfo(name = "newsImage")
    public String newsImgUrl;
    @ColumnInfo(name = "newsUrl")
    public String newsUrl;
    @ColumnInfo(name = "newsDescription")
    public String newsDescription;

    @ColumnInfo(name = "newsContent")
    public String newsContent;

    public savednews(@NonNull String newsTitle, String newsImgUrl, String newsUrl, String newsDescription, String newsContent) {
        this.newsTitle = newsTitle;
        this.newsImgUrl = newsImgUrl;
        this.newsUrl = newsUrl;
        this.newsDescription = newsDescription;
        this.newsContent = newsContent;
    }

    @Override
    public String toString() {
        return "savedNews{" +
                "newsTitle='" + newsTitle + '\'' +
                ", newsImgUrl='" + newsImgUrl + '\'' +
                ", newsUrl='" + newsUrl + '\'' +
                ", newsDescription='" + newsDescription + '\'' +
                ", newsContent='" + newsContent + '\'' +
                '}';
    }
}
