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

    public BookResponse(int id,
                        String title,
                        String author,
                        String description,
                        float rating,
                        String imageUrl)
    {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.rating = rating;
        this.imageUrl = imageUrl;
    }

    public Book toBook() {
        return new Book(id, title, author, description, rating, imageUrl, false);
    }
}