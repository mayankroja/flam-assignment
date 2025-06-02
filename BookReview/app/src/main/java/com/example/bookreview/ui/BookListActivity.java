package com.example.bookreview.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookreview.R;
import com.example.bookreview.data.BookRepository;
import com.example.bookreview.viewmodel.BookViewModel;

public class BookListActivity extends AppCompatActivity {
    private BookViewModel viewModel;
    private BookAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        progressBar = findViewById(R.id.progressBar);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with click listener
        adapter = new BookAdapter(book -> {
            Intent intent = new Intent(BookListActivity.this, BookDetailActivity.class);
            intent.putExtra("BOOK_ID", book.id);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // Initialize ViewModel
        BookRepository repository = new BookRepository(this);
        viewModel = new ViewModelProvider(
                this,
                new ViewModelProvider.Factory() {
                    @Override
                    public <T extends ViewModel> T create(Class<T> modelClass) {
                        return (T) new BookViewModel(repository);
                    }
                }
        ).get(BookViewModel.class);

        // Observe books
        viewModel.getBooks().observe(this, books -> {
            Log.d("BookListActivity", "Books received: " + books.size());
            adapter.submitList(books);
            progressBar.setVisibility(View.GONE);
        });
    }
}