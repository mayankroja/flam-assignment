package com.example.bookreview.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BookDao {
    @Query("SELECT * FROM books")
    LiveData<List<Book>> getAllBooks();

    @Query("SELECT * FROM books WHERE id = :bookId")
    LiveData<Book> getBookById(int bookId);

    @Query("SELECT * FROM books WHERE is_favorite = 1")
    LiveData<List<Book>> getFavoriteBooks();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Book> books);

    @Update
    void update(Book book);

    // For internal repository use
    @Query("SELECT * FROM books")
    List<Book> getAllBooksSync();
}