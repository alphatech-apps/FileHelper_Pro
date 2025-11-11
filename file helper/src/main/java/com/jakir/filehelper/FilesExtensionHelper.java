package com.jakir.filehelper;

import java.io.File;
import java.util.Locale;

public class FilesExtensionHelper {
    // ---------- get Extansion ----------
    public static String getExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf(".");
        return (lastDot > 0 && lastDot < name.length() - 1) ? name.substring(lastDot + 1).toLowerCase() : "";
    }

    // ---------- SVG ----------
    public static boolean isSvgFile(String name) {
        if (name == null) return false;
        return name.toLowerCase(Locale.ROOT).endsWith(".svg");
    }

    // ---------- IMAGE ----------
    public static boolean isImageFile(String name) {
        if (name == null) return false;
        name = name.toLowerCase(Locale.ROOT);
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif") || name.endsWith(".bmp") || name.endsWith(".webp") || name.endsWith(".heic") || name.endsWith(".heif") || name.endsWith(".tiff") || name.endsWith(".tif") || name.endsWith(".ico") || name.endsWith(".cr2") || name.endsWith(".nef") || name.endsWith(".arw");
    }

    // ---------- VIDEO ----------
    public static boolean isVideoFile(String name) {
        if (name == null) return false;
        name = name.toLowerCase(Locale.ROOT);
        return name.endsWith(".mp4") || name.endsWith(".avi") || name.endsWith(".mkv") || name.endsWith(".mov") || name.endsWith(".flv") || name.endsWith(".wmv") || name.endsWith(".webm") || name.endsWith(".m4v") || name.endsWith(".3gp") || name.endsWith(".ts") || name.endsWith(".mts") || name.endsWith(".m2ts") || name.endsWith(".ogv") || name.endsWith(".vob");
    }

    // ---------- AUDIO ----------
    public static boolean isAudioFile(String name) {
        if (name == null) return false;
        name = name.toLowerCase(Locale.ROOT);
        return name.endsWith(".mp3") || name.endsWith(".wav") || name.endsWith(".aac") || name.endsWith(".ogg") || name.endsWith(".m4a") || name.endsWith(".flac") || name.endsWith(".amr") || name.endsWith(".opus") || name.endsWith(".wma");
    }

    // ---------- DOCUMENT ----------
    public static boolean isDocumentFile(String name) {
        if (name == null) return false;
        name = name.toLowerCase(Locale.ROOT);
        return name.endsWith(".pdf") || name.endsWith(".doc") || name.endsWith(".docx") || name.endsWith(".xls") || name.endsWith(".xlsx") || name.endsWith(".ppt") || name.endsWith(".pptx") || name.endsWith(".txt") || name.endsWith(".rtf") || name.endsWith(".odt") || name.endsWith(".csv") || name.endsWith(".html") || name.endsWith(".xml") || name.endsWith(".json");
    }

    // ---------- APK ----------
    public static boolean isApkFile(String name) {
        if (name == null) return false;
        return name.toLowerCase(Locale.ROOT).endsWith(".apk");
    }

    // ---------- COMPRESSED ----------
    public static boolean isCompressedFile(String name) {
        if (name == null) return false;
        name = name.toLowerCase(Locale.ROOT);
        return name.endsWith(".zip") || name.endsWith(".rar") || name.endsWith(".7z") || name.endsWith(".tar") || name.endsWith(".gz") || name.endsWith(".bz2") || name.endsWith(".xz") || name.endsWith(".iso");
    }

}
