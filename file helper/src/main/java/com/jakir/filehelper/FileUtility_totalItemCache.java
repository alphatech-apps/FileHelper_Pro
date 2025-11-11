package com.jakir.filehelper;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//
// Created by JAKIR HOSSAIN on 11/9/2025.
//
public class FileUtility_totalItemCache {

    private static final int CACHE_LIMIT = 500;
    private static final LinkedHashMap<String, String> folderItemCache = new LinkedHashMap<String, String>(CACHE_LIMIT, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            return size() > CACHE_LIMIT; // Auto remove oldest cache entry
        }
    };

    private static final ExecutorService executor = Executors.newFixedThreadPool(2);
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    // 🔹 Get total items inside a folder (your existing logic)
    public static String getTotalItemsInsideFolder(File folder) {
        if (folder == null || !folder.isDirectory()) return "";
        File[] files = folder.listFiles();
        if (files == null) return "Empty folder";

        int count = files.length;
        return count + (count == 1 ? " item" : " items");
    }

    // 🔹 Get cached item count instantly, then refresh in background
    public static void getCachedFolderItemCount(File file, TextView textView, Object tag) {
        if (file == null || textView == null) return;

        String path = file.getPath();
        String cachedCount;

        synchronized (folderItemCache) {
            cachedCount = folderItemCache.get(path);
        }

        // Show cached count instantly if available
        if (cachedCount != null) {
            textView.setText(cachedCount);
        } else {
            textView.setText("..."); // Optional loading indicator
        }

        // Background refresh
        executor.execute(() -> {
            String newCount = getTotalItemsInsideFolder(file);

            synchronized (folderItemCache) {
                folderItemCache.put(path, newCount);
            }

            mainHandler.post(() -> {
                if (textView.getTag() != null && textView.getTag().equals(tag)) {
                    textView.setText(newCount);
                }
            });
        });
    }

    // 🔹 Clear cache manually (optional)
    public static void clearFolderCache() {
        synchronized (folderItemCache) {
            folderItemCache.clear();
        }
    }
}
