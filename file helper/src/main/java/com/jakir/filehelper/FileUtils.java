package com.jakir.filehelper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;

import java.io.File;

//
// Created by JAKIR HOSSAIN on 11/14/2025.
//
public class FileUtils {
    public static String getPathFromUri(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex("_data");
                    if (idx != -1) result = cursor.getString(idx);
                }
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
        }
        return result;
    }

    public static String getExtensionFromUri(Context context, Uri uri) {
        String extension = "";

        // 1. Content scheme (content://) handle করা
        if (uri.getScheme().equals("content")) {
            String type = context.getContentResolver().getType(uri); // MIME type
            if (type != null) {
                extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(type);
            }
        }

        // 2. File scheme (file://) বা fallback
        if (extension == null || extension.isEmpty()) {
            String path = uri.getPath();
            if (path != null) {
                int lastDot = path.lastIndexOf('.');
                if (lastDot > 0 && lastDot < path.length() - 1) {
                    extension = path.substring(lastDot + 1).toLowerCase();
                }
            }
        }

        return extension != null ? extension : "";
    }

    public static String getFileNameFromUri(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) result = cursor.getString(index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result == null) result = uri.getLastPathSegment();
        return result;
    }

    public static Uri getUriFromPath(Context context, String FilePath) {
        if (FilePath != null) {
            File file = new File(FilePath);
            return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        }
        return null;
    }

    public static boolean isFileWritable(String path) {
        if (path == null) return false;
        File file = new File(path);
        return file.exists() && file.canWrite();
    }


    public static String getMimeType_uri(Context context, Uri uri) {
        String type = null;

        // 1. content resolver দিয়ে mime type বের করা
        if (uri.getScheme() != null && uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            type = context.getContentResolver().getType(uri);
        }

        // 2. file extension fallback
        if (type == null) {
            String path = uri.getPath();
            if (path != null) {
                String extension = MimeTypeMap.getFileExtensionFromUrl(path);
                if (extension != null) {
                    type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
                }
            }
        }

        if (type == null) type = "*/*"; // ultimate fallback

//        Log.d("getMimeType", "MIME: " + type);
        return type;
    }

    public static String getMimeType_file(Context context, File file) {
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        return getMimeType_uri(context, uri);
    }
}
