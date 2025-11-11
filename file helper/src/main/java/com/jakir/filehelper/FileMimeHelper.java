package com.jakir.filehelper;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;

import java.io.File;

//
// Created by JAKIR HOSSAIN on 11/9/2025.
//
public class FileMimeHelper {

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
