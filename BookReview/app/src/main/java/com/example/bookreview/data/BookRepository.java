package com.example.bookreview.data;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.bookreview.network.ApiService;
import com.example.bookreview.network.BookResponse;
import com.example.bookreview.network.MockApiService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class BookRepository {
    private final BookDao bookDao;
    private final ApiService apiService;
    private final Executor executor;
    private static final String TAG = "BookRepository";

    public BookRepository(Context context) {
        BookDatabase database = BookDatabase.getDatabase(context);
        this.bookDao = database.bookDao();
        this.apiService = new MockApiService(); // Use mock service
        this.executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Book>> getAllBooks() {
        refreshBooks();
        return bookDao.getAllBooks();
    }

    public LiveData<Book> getBookById(int id) {
        return bookDao.getBookById(id);
    }

    public LiveData<List<Book>> getFavoriteBooks() {
        return bookDao.getFavoriteBooks();
    }

    // In BookRepository.java, modify refreshBooks():
    private void refreshBooks() {
        executor.execute(() -> {
            try {
                Log.d("BookRepository", "Attempting to fetch books");
                Response<List<BookResponse>> response = apiService.getBooks().execute();
                Log.d("BookRepository", "Response received: " + response.isSuccessful());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("BookRepository", "Books count: " + response.body().size());
                    List<Book> books = convertToEntities(response.body());
                    syncWithLocalData(books);
                }
            } catch (IOException e) {
                Log.e("BookRepository", "Error fetching books", e);
            }
        });
    }

    private List<Book> convertToEntities(List<BookResponse> responses) {
        List<Book> books = new ArrayList<>();
        for (BookResponse response : responses) {
            books.add(response.toBook());
        }
        return books;
    }

    private void syncWithLocalData(List<Book> networkBooks) {
        // Get current favorites
        List<Book> localBooks = bookDao.getAllBooksSync();
        Map<Integer, Book> localMap = new HashMap<>();
        for (Book book : localBooks) {
            localMap.put(book.id, book);
        }

        // Update network books with local favorite status
        for (Book networkBook : networkBooks) {
            Book localBook = localMap.get(networkBook.id);
            if (localBook != null) {
                networkBook.isFavorite = localBook.isFavorite;
            }
        }

        // Save to database
        bookDao.insert(networkBooks);
    }

    public void toggleFavorite(Book book) {
        executor.execute(() -> {
            book.isFavorite = !book.isFavorite;
            bookDao.update(book);
        });
    }
}