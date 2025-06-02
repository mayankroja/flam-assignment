package com.example.bookreview.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;

import com.example.bookreview.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {
    private static final String TAG = "ImageLoader";
    private static final ExecutorService executor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final LruCache<String, Bitmap> memoryCache;
    private static final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    static {
        // Use 1/8th of available memory for cache
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    /**
     * Public entry point to load an image from a URL (or show an error placeholder).
     *
     * @param urlString        The HTTP/HTTPS URL to fetch; can be null/empty.
     * @param imageView        The ImageView to populate.
     * @param placeholderResId Drawable resource ID for placeholder image.
     * @param errorResId       Drawable resource ID for error image.
     */
    public static void loadImage(
            @Nullable final String urlString,
            final ImageView imageView,
            @DrawableRes int placeholderResId,
            @DrawableRes int errorResId
    ) {
        // 1. Immediately tag the ImageView
        imageView.setTag(R.id.image_loader_tag_url, urlString);

        // 2. Show placeholder right away
        try {
            imageView.setImageResource(placeholderResId);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set placeholder resource: " + placeholderResId, e);
        }

        // 3. If URL is empty or null, show error and bail
        if (urlString == null || urlString.isEmpty()) {
            setErrorImage(imageView, urlString, errorResId);
            return;
        }

        // 4. Check in-memory cache first
        Bitmap cachedBitmap = memoryCache.get(urlString);
        if (cachedBitmap != null) {
            // Make sure the ImageView still wants this URL
            if (Objects.equals(imageView.getTag(R.id.image_loader_tag_url), urlString)) {
                imageView.setImageBitmap(cachedBitmap);
            }
            return;
        }

        // 5. Otherwise, fetch from network on background thread
        executor.execute(() -> {
            // a) Confirm tag hasn’t changed before downloading
            String currentTag = (String) imageView.getTag(R.id.image_loader_tag_url);
            if (!Objects.equals(currentTag, urlString)) {
                return;
            }

            Bitmap downloaded = null;
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream input = conn.getInputStream();
                downloaded = BitmapFactory.decodeStream(input);
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Network error fetching " + urlString, e);
            }

            if (downloaded != null) {
                // Cache it
                memoryCache.put(urlString, downloaded);

                // a. Post result to UI thread if tag still matches
                Bitmap finalDownloaded = downloaded;
                mainThreadHandler.post(() -> {
                    if (Objects.equals(imageView.getTag(R.id.image_loader_tag_url), urlString)) {
                        imageView.setImageBitmap(finalDownloaded);
                    }
                });
            } else {
                // b. On decode failure or any exception, show error placeholder
                mainThreadHandler.post(() -> {
                    setErrorImage(imageView, urlString, errorResId);
                });
            }
        });
    }

    private static void setErrorImage(ImageView imageView, String tagForUrl, @DrawableRes int errorResId) {
        // Only set error if tag still matches (otherwise this View got recycled)
        if (Objects.equals(imageView.getTag(R.id.image_loader_tag_url), tagForUrl)) {
            try {
                imageView.setImageResource(errorResId);
            } catch (Exception e) {
                Log.e(TAG, "Failed to set error placeholder: " + errorResId, e);
            }
        }
    }
}
