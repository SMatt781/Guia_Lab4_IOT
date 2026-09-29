package com.example.catalogoproductos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

// HttpURLConnection + ExecutorService (semana 4), sin una biblioteca extra de imágenes.
public final class ImageLoader {
    private ImageLoader() {}

    public static void load(ImageView image, String url) {
        String requestTag = UUID.randomUUID().toString();
        image.setTag(requestTag);
        image.setImageResource(R.drawable.image_placeholder);
        if (url == null || !url.startsWith("https://")) return;
        WeakReference<ImageView> reference = new WeakReference<>(image);
        LabApplication app = (LabApplication) image.getContext().getApplicationContext();
        app.executor.execute(() -> {
            HttpURLConnection connection = null;
            Bitmap bitmap = null;
            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                if (connection.getResponseCode() == 200) {
                    try (InputStream stream = connection.getInputStream()) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        bitmap = BitmapFactory.decodeStream(stream, null, options);
                    }
                }
            } catch (Exception ignored) {
                // Una imagen fallida conserva el placeholder; la lista sigue funcionando.
            } finally {
                if (connection != null) connection.disconnect();
            }
            Bitmap result = bitmap;
            new Handler(Looper.getMainLooper()).post(() -> {
                ImageView target = reference.get();
                if (target != null && requestTag.equals(target.getTag()) && result != null) {
                    target.setImageBitmap(result);
                }
            });
        });
    }
}
