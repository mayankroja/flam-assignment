package com.example.bookreview.network;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.mock.Calls;

public class MockApiService implements ApiService {
    @Override
    public Call<List<BookResponse>> getBooks() {
        List<BookResponse> books = new ArrayList<>();
        books.add(new BookResponse(1, "Clean Code", "Robert C. Martin",
                "A handbook of agile software craftsmanship", 4.7f,
                "https://picsum.photos/seed/picsum/200/300"));
        books.add(new BookResponse(2, "Design Patterns", "Erich Gamma",
                "Elements of reusable object-oriented software", 4.5f,
                "https://picsum.photos/seed/picsum/200/300"));
        Log.d("mockapiservice", "getBooks: "+books);
        return Calls.response(books);
    }
}