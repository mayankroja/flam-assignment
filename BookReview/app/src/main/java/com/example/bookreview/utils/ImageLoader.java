package com.example.bookreview.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable; // Import Nullable
import androidx.collection.LruCache;

import com.example.bookreview.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Objects; // Import Objects for equals

public class ImageLoader {
    private static final String TAG = "ImageLoader";
    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final LruCache<String, Bitmap> memoryCache;
    private static final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    static {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    private static final @DrawableRes int DEFAULT_PLACEHOLDER = R.drawable.placeholder;
    private static final @DrawableRes int DEFAULT_ERROR_PLACEHOLDER = R.drawable.error_placeholder;

    public static void loadImage(final String urlString, final ImageView imageView) {
        loadImage(urlString, imageView, DEFAULT_PLACEHOLDER, DEFAULT_ERROR_PLACEHOLDER);
    }

    public static void loadImage(@Nullable final String urlString, final ImageView imageView, // Allow urlString to be nullable
                                 @DrawableRes int placeholderResId, @DrawableRes int errorResId) {
        if (imageView == null) {
            return;
        }

        // Store the original URL (even if null) in the ImageView's tag.
        // This helps ensure that the correct image/placeholder is displayed if the ImageView is recycled.
        imageView.setTag(R.id.image_loader_tag_url, urlString);

        try {
            imageView.setImageResource(placeholderResId);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set placeholder resource: " + placeholderResId, e);
            try {
                imageView.setImageResource(android.R.drawable.ic_menu_gallery);
            } catch (Exception ignored) {}
        }

        if (urlString == null || urlString.isEmpty()) {
            Log.w(TAG, "URL is null or empty. Setting error placeholder.");
            // Pass the original (potentially null) urlString for the tag check
            setErrorImage(imageView, urlString, errorResId);
            return;
        }

        Bitmap cachedBitmap = getBitmapFromMemCache(urlString);
        if (cachedBitmap != null) {
            if (shouldUpdateImageView(imageView, urlString)) { // urlString is non-null here
                imageView.setImageBitmap(cachedBitmap);
            }
            return;
        }

        executor.execute(() -> {
            // Re-check the tag inside the executor thread before network call,
            // as the imageView might have been recycled.
            final String currentUrlForView = (String) imageView.getTag(R.id.image_loader_tag_url);
            if (!Objects.equals(urlString, currentUrlForView)) {
                // The ImageView has been recycled for a different URL or cleared
                Log.d(TAG, "ImageView recycled or URL changed before network load for: " + urlString);
                return;
            }

            try {
                URL url = new URL(urlString); // urlString is guaranteed non-null & non-empty here
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                connection.disconnect();

                if (bitmap != null) {
                    addBitmapToMemoryCache(urlString, bitmap);
                    mainThreadHandler.post(() -> {
                        if (shouldUpdateImageView(imageView, urlString)) {
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                } else {
                    Log.w(TAG, "Bitmap could not be decoded from URL: " + urlString);
                    setErrorImageOnMainThread(imageView, urlString, errorResId);
                }
            } catch (OutOfMemoryError oom) {
                Log.e(TAG, "OutOfMemoryError while loading image: " + urlString, oom);
                memoryCache.evictAll();
                setErrorImageOnMainThread(imageView, urlString, errorResId);
            } catch (Exception e) {
                Log.e(TAG, "Error loading image: " + urlString, e);
                setErrorImageOnMainThread(imageView, urlString, errorResId);
            }
        });
    }

    private static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null && bitmap != null && key !=null) { // Ensure key is not null
            memoryCache.put(key, bitmap);
        }
    }

    private static Bitmap getBitmapFromMemCache(String key) {
        if (key == null) return null; // Ensure key is not null
        return memoryCache.get(key);
    }

    // urlStringForComparison is the original URL this operation was initiated for.
    // It can be null if the original request was for a null/empty URL (error placeholder case).
    private static boolean shouldUpdateImageView(ImageView imageView, @Nullable String urlStringForComparison) {
        final Object tag = imageView.getTag(R.id.image_loader_tag_url);
        // Use Objects.equals for null-safe comparison
        return Objects.equals(urlStringForComparison, tag);
    }

    private static void setErrorImageOnMainThread(ImageView imageView, @Nullable String urlStringForTag, @DrawableRes int errorResId) {
        mainThreadHandler.post(() -> setErrorImage(imageView, urlStringForTag, errorResId));
    }

    private static void setErrorImage(ImageView imageView, @Nullable String urlStringForTag, @DrawableRes int errorResId) {
        if (shouldUpdateImageView(imageView, urlStringForTag)) { // urlStringForTag can be null here
            try {
                imageView.setImageResource(errorResId);
            } catch (Exception e) {
                Log.e(TAG, "Failed to set error resource: " + errorResId, e);
                try {
                    imageView.setImageResource(android.R.drawable.ic_dialog_alert);
                } catch (Exception ex) {
                    Log.e(TAG, "Failed to set system fallback error icon.", ex);
                    imageView.setImageDrawable(null);
                }
            }
        } else {
            Log.d(TAG, "ImageView recycled or URL changed before setting error placeholder for (original): " + urlStringForTag);
        }
    }
}