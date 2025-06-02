package com.example.bookreview.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.bookreview.data.Book;
import com.example.bookreview.data.BookRepository;

import java.util.List;

public class BookViewModel extends ViewModel {
    private final BookRepository repository;
    private final LiveData<List<Book>> books;

    public BookViewModel(BookRepository repository) {
        this.repository = repository;
        this.books = repository.getAllBooks();
    }

    public LiveData<List<Book>> getBooks() {
        return books;
    }

    public LiveData<Book> getBookById(int id) {
        return repository.getBookById(id);
    }

    public void toggleFavorite(Book book) {
        repository.toggleFavorite(book);
    }
}