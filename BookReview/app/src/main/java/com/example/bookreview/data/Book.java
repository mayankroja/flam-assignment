package com.example.bookreview.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "books")
public class Book {
    @PrimaryKey
    public int id;

    public String title;
    public String author;
    public String description;
    public float rating;
    public String imageUrl;

    @ColumnInfo(name = "is_favorite")
    public boolean isFavorite;

    public Book() {} // Required by Room

    public Book(int id, String title, String author, String description,
                float rating, String imageUrl, boolean isFavorite) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.isFavorite = isFavorite;
    }
}