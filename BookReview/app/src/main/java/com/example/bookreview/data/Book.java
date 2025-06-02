package com.example.bookreview.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "books")
public class Book {
    @PrimaryKey
    public int id;
    @ColumnInfo(name = "title")
    public String title;
    @ColumnInfo(name = "author")
    public String author;
    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "rating")
    public float rating;
    @ColumnInfo(name = "image_url")
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