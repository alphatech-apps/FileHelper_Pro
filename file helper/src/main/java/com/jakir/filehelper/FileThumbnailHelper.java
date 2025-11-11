package com.jakir.filehelper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import com.caverock.androidsvg.SVG;

import java.io.InputStream;

//
// Created by JAKIR HOSSAIN on 11/9/2025.
//
public class FileThumbnailHelper {
    private static final int THUMB_SIZE = 200; // 200x200

    // ---------- VIDEO ----------
    public static Drawable getVideoThumbnail(Uri uri, Context context) {

        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, uri);
            Bitmap bitmap = retriever.getFrameAtTime();
            retriever.release();

            if (bitmap != null) {
                // Center crop
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int newSize = Math.min(width, height);

                int x = (width - newSize) / 2;
                int y = (height - newSize) / 2;

                Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, x, y, newSize, newSize);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(croppedBitmap, THUMB_SIZE, THUMB_SIZE, true);
                return new BitmapDrawable(context.getResources(), scaledBitmap);
            } else {
                return null; // fallback
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return null; // fallback
        }
    }

    // ---------- IMAGE ----------
    public static Drawable getImageThumbnail(Uri uri, Context context) {
        try (InputStream input = context.getContentResolver().openInputStream(uri)) {
            if (input == null) return null; // fallback

            Bitmap bitmap = BitmapFactory.decodeStream(input);
            if (bitmap == null) return null; // fallback

            // Center crop
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int newSize = Math.min(width, height);

            int x = (width - newSize) / 2;
            int y = (height - newSize) / 2;

            Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, x, y, newSize, newSize);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(croppedBitmap, THUMB_SIZE, THUMB_SIZE, true);
            return new BitmapDrawable(context.getResources(), scaledBitmap);

        } catch (Exception ex) {
            ex.printStackTrace();
            return null; // fallback
        }
    }

    // ---------- SVG ----------
    public static Drawable getSvgThumbnail(Uri uri, Context context) {
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            SVG svg = SVG.getFromInputStream(inputStream);

        /*    // Fallback size if not set
            if (svg.getDocumentWidth() <= 0 || svg.getDocumentHeight() <= 0) {
                svg.setDocumentWidth(200f);
                svg.setDocumentHeight(200f);
            }*/
            Picture picture = svg.renderToPicture();
            return new PictureDrawable(picture);

        } catch (Exception e) {
        }
        return null;
    }

    // ---------- PDF ----------
    public static Drawable getPdfThumbnail(Context context) {
        // Generating real PDF preview needs PdfRenderer API (Android 5+)
        // For simplicity: return pdf icon fallback
        return null; // fallback
    }

    // ---------- APK ----------
    public static Drawable getApkThumbnail(Uri uri, Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(uri.getPath(), 0);
            if (info != null) {
                info.applicationInfo.sourceDir = uri.getPath();
                info.applicationInfo.publicSourceDir = uri.getPath();
                Drawable icon = info.applicationInfo.loadIcon(pm);
                if (icon != null) return icon;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // fallback
    }

    // ---------- AUDIO ----------
    public static Drawable getAudioThumbnail(Uri uri, Context context) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, uri);
            byte[] art = retriever.getEmbeddedPicture();
            retriever.release();

            if (art != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
                return new BitmapDrawable(context.getResources(), scaled);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // fallback
    }

}
