package com.example.bookreview.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookreview.R;
import com.example.bookreview.data.Book; // Assuming your Book class is here
import com.example.bookreview.utils.ImageLoader;

import java.util.Objects; // <-- IMPORT THIS

public class BookAdapter extends ListAdapter<Book, BookAdapter.BookViewHolder> {
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Book book);
    }

    public BookAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Book> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Book>() {
                @Override
                public boolean areItemsTheSame(@NonNull Book oldItem, @NonNull Book newItem) {
                    // Assuming 'id' is a primitive type like 'long' or 'int'.
                    // If 'id' is an object (e.g., String, Long),
                    // you should use Objects.equals(oldItem.id, newItem.id) here too.
                    // For now, assuming it's a primitive and non-null.
                    return oldItem.id == newItem.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull Book oldItem, @NonNull Book newItem) {
                    // Use Objects.equals for null-safe comparison of strings
                    return Objects.equals(oldItem.title, newItem.title) &&
                            Objects.equals(oldItem.author, newItem.author) &&
                            Objects.equals(oldItem.imageUrl, newItem.imageUrl) &&
                            oldItem.isFavorite == newItem.isFavorite; // isFavorite is a boolean, direct comparison is fine
                }
            };

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = getItem(position);
        if (book != null) { // Good practice to check for null item from getItem()
            holder.bind(book, listener);
        }
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView authorTextView;
        private final ImageView thumbnailImageView;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);
        }

        public void bind(Book book, OnItemClickListener listener) {
            // Consider null checks here too if data can be null and TextViews don't handle it
            titleTextView.setText(book.title != null ? book.title : ""); // Display empty string if title is null
            authorTextView.setText(book.author != null ? book.author : ""); // Display empty string if author is null

            // ImageLoader should ideally also handle null or empty imageUrls gracefully
            ImageLoader.loadImage(book.imageUrl, thumbnailImageView);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(book);
                }
            });
        }
    }
}