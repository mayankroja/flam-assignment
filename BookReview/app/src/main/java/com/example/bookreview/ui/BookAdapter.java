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
import com.example.bookreview.data.Book;
import com.example.bookreview.utils.ImageLoader;

import java.util.Objects;

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
                    return oldItem.id == newItem.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull Book oldItem, @NonNull Book newItem) {

                    return Objects.equals(oldItem.title, newItem.title) &&
                            Objects.equals(oldItem.author, newItem.author) &&
                            Objects.equals(oldItem.imageUrl, newItem.imageUrl) &&
                            oldItem.isFavorite == newItem.isFavorite;
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
        if (book != null) {
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

            titleTextView.setText(book.title != null ? book.title : "");
            authorTextView.setText(book.author != null ? book.author : "");


            ImageLoader.loadImage(book.imageUrl, thumbnailImageView);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(book);
                }
            });
        }
    }
}