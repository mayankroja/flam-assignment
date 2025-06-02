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
                "https://picsum.photos/id/237/200/300"));
        books.add(new BookResponse(2, "Design Patterns", "Erich Gamma",
                "Elements of reusable object-oriented software", 4.5f,
                "https://picsum.photos/id/237/200/300"));
        books.add(new BookResponse(3, "Design Patterns", "Erich Gamma",
                "Elements of reusable object-oriented software", 4.5f,
                "https://picsum.photos/id/237/200/300"));
        books.add(new BookResponse(4, "Design Patterns", "Erich Gamma",
                "Elements of reusable object-oriented software", 4.5f,
                "https://picsum.photos/id/237/200/300"));
        books.add(new BookResponse(5, "Design Patterns", "Erich Gamma",
                "Elements of reusable object-oriented software", 4.5f,
                "https://picsum.photos/id/237/200/300"));
        books.add(new BookResponse(6, "Design Patterns", "Erich Gamma",
                "Elements of reusable object-oriented software", 4.5f,
                "https://picsum.photos/id/237/200/300"));
        books.add(new BookResponse(7, "Design Patterns", "Erich Gamma",
                "Elements of reusable object-oriented software", 4.5f,
                "https://picsum.photos/id/237/200/300"));
        books.add(new BookResponse(8, "Design Patterns", "Erich Gamma",
                "Elements of reusable object-oriented software", 4.5f,
                "https://picsum.photos/id/237/200/300"));
        books.add(new BookResponse(9, "Design Patterns", "Erich Gamma",
                "Elements of reusable object-oriented software", 4.5f,
                "https://picsum.photos/id/237/200/300"));
        Log.d("mockapiservice", "getBooks: "+books);
        return Calls.response(books);
    }
}