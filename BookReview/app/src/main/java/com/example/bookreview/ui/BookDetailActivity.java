package com.example.bookreview.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.bookreview.R;
import com.example.bookreview.data.Book;
import com.example.bookreview.data.BookRepository;
import com.example.bookreview.utils.ImageLoader;
import com.example.bookreview.viewmodel.BookViewModel;

public class BookDetailActivity extends AppCompatActivity {
    private BookViewModel viewModel;
    private Book currentBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        int bookId = getIntent().getIntExtra("BOOK_ID", -1);
        if (bookId == -1) {
            finish();
            return;
        }

        ProgressBar progressBar = findViewById(R.id.progressBar);
        ImageView imageView = findViewById(R.id.imageView);
        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView authorTextView = findViewById(R.id.authorTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        TextView ratingTextView = findViewById(R.id.ratingTextView);
        Button favoriteButton = findViewById(R.id.favoriteButton);

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

        viewModel.getBookById(bookId).observe(this, book -> {
            if (book != null) {
                currentBook = book;
                titleTextView.setText(book.title);
                authorTextView.setText(book.author);
                descriptionTextView.setText(book.description);
                ratingTextView.setText(String.format("Rating: %.1f", book.rating));
                ImageLoader.loadImage(book.imageUrl, imageView);
                favoriteButton.setText(book.isFavorite ? "Remove Favorite" : "Add Favorite");
                progressBar.setVisibility(View.GONE);
            }
        });

        favoriteButton.setOnClickListener(v -> {
            if (currentBook != null) {
                viewModel.toggleFavorite(currentBook);
                favoriteButton.setText(currentBook.isFavorite ? "Remove Favorite" : "Add Favorite");
            }
        });
    }
}