package com.example.bookreview.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("books")
    Call<List<BookResponse>> getBooks();
}