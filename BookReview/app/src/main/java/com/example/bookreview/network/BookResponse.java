package com.example.bookreview.network;

import com.example.bookreview.data.Book;
import com.google.gson.annotations.SerializedName;

public class BookResponse {
    @SerializedName("id")
    public int id;

    @SerializedName("title")
    public String title;

    @SerializedName("author")
    public String author;

    @SerializedName("description")
    public String description;

    @SerializedName("rating")
    public float rating;

    @SerializedName("image_url")
    public String imageUrl;

    public BookResponse(int i, String cleanCode, String s, String s1, float v, String url) {
    }

    public Book toBook() {
        return new Book(id, title, author, description, rating, imageUrl, false);
    }
}