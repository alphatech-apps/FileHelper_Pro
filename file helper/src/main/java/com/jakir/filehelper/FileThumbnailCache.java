package com.jakir.filehelper;

import android.graphics.drawable.Drawable;
import android.util.LruCache;

//
// Created by JAKIR HOSSAIN on 9/27/2025.
//
public class FileThumbnailCache {

    private static final LruCache<String, Drawable> thumbnailCache;

    static {
        // Max memory 1/8 use thumbnail caching
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        thumbnailCache = new LruCache<String, Drawable>(cacheSize) {
            @Override
            protected int sizeOf(String key, Drawable value) {
                return value.getIntrinsicHeight() * value.getIntrinsicWidth() * 4 / 1024;
            }
        };
    }

    public static Drawable getThumbnail(String key) {
        return thumbnailCache.get(key);
    }

    public static void putThumbnail(String key, Drawable drawable) {
        if (getThumbnail(key) == null) {
            thumbnailCache.put(key, drawable);
        }
    }
}
