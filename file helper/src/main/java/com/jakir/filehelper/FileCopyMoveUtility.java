package com.jakir.filehelper;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileCopyMoveUtility {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());


    public static void safeCopyOrMoveAsync(Context context, File source, File dest, boolean isMove, long totalSize, AtomicBoolean isCancelled, CopyProgressListener listener, CopyResultCallback callback) {

        executor.submit(() -> {
            boolean result = copyOrMove(source, dest, isMove, isCancelled, listener, totalSize);

            // Cancel cleanup
            if (isCancelled.get() && dest.exists()) {
                deleteRecursive(dest);
                result = false;
            }

            boolean finalResult = result;
            long fileOriginSize = FileUtility.getTotalSize(dest);
            mainHandler.post(() -> callback.onComplete(finalResult, fileOriginSize));
        });
    }

    private static boolean copyOrMove(File source, File dest, boolean isMove, AtomicBoolean isCancelled, CopyProgressListener listener, long totalSize) {

        if (isCancelled.get()) return false;

        long[] copiedSize = {0};

        if (source.isDirectory()) {
            return copyOrMoveFolder(source, dest, isMove, isCancelled, listener, copiedSize, totalSize);
        } else {
            return copyOrMoveFile(source, dest, isMove, isCancelled, listener, copiedSize, totalSize);
        }
    }

    private static boolean copyOrMoveFolder(File source, File dest, boolean isMove, AtomicBoolean isCancelled, CopyProgressListener listener, long[] copiedSize, long totalSize) {

        if (isCancelled.get()) return false;

        // Fast move try
        if (isMove && source.renameTo(dest)) {
            if (listener != null)
                updateListener(listener, FileUtility.getTotalSize(dest), totalSize, source.getName());
            return true;
        }

        if (!dest.exists()) dest.mkdirs();

        File[] files = source.listFiles();
        if (files != null) {
            for (File file : files) {
                if (isCancelled.get()) return false;

                File newDest = new File(dest, file.getName());
                boolean success;
                if (file.isDirectory()) {
                    // Recursive call for subfolder
                    success = copyOrMoveFolder(file, newDest, isMove, isCancelled, listener, copiedSize, totalSize);
                } else {
                    // Copy single file
                    success = copyOrMoveFile(file, newDest, isMove, isCancelled, listener, copiedSize, totalSize);
                }

                if (!success && isCancelled.get()) {
                    deleteRecursive(dest);
                    return false;
                }
            }
        }

        if (isMove) deleteRecursive(source);

        return true;
    }

    private static boolean copyOrMoveFile(File source, File dest, boolean isMove, AtomicBoolean isCancelled, CopyProgressListener listener, long[] copiedSize, long totalSize) {

        if (isCancelled.get()) return false;

        // Fast move try
        if (isMove && source.renameTo(dest)) {
            copiedSize[0] += source.length();
            updateListener(listener, copiedSize[0], totalSize, source.getName());
            return true;
        }

        try (FileInputStream in = new FileInputStream(source); FileOutputStream out = new FileOutputStream(dest)) {

            // Dynamic buffer size
            long totalBytes = source.length();
            int bufferSize = totalBytes < 10 * 1024 * 1024 ? 8 * 1024 :    // <10MB → 8KB
                    totalBytes < 100 * 1024 * 1024 ? 16 * 1024 :  // <100MB → 16KB
                            32 * 1024;                                    // ≥100MB → 32KB

            byte[] buffer = new byte[bufferSize];
            int length;
            long lastUpdate = System.currentTimeMillis();

            while ((length = in.read(buffer)) > 0) {
                if (isCancelled.get()) {
                    if (dest.exists()) dest.delete();
                    return false;
                }

                out.write(buffer, 0, length);
                copiedSize[0] += length;

                // Update progress every 500ms (not every chunk)
                long now = System.currentTimeMillis();
                if (now - lastUpdate > 500) {
                    updateListener(listener, copiedSize[0], totalSize, source.getName());
                    lastUpdate = now;
                }
            }
            out.flush();

            // Final 100% update
            updateListener(listener, copiedSize[0], totalSize, source.getName());

        } catch (IOException e) {
            e.printStackTrace();
            if (dest.exists()) dest.delete();
            return false;
        }

        if (isMove) source.delete();
        return true;
    }

    private static void updateListener(CopyProgressListener listener, long copiedSize, long totalSize, String name) {
        if (listener != null) {
            mainHandler.post(() -> listener.onProgress(copiedSize, totalSize, name));
        }
    }

    public static void deleteRecursive(File fileOrDir) {
        if (fileOrDir.isDirectory()) {
            File[] children = fileOrDir.listFiles();
            if (children != null) for (File child : children) deleteRecursive(child);
        }
        fileOrDir.delete();
    }

    public interface CopyProgressListener {
        void onProgress(long copiedBytes, long totalBytes, String name);
    }

    public interface CopyResultCallback {
        void onComplete(boolean success, long fileOriginSize);
    }
}
