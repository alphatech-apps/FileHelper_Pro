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

public class FileCopyMoveUtility2 {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());


    public static void safeCopyOrMoveAsync(Context context, File source, File dest, boolean isMove, long totalSize, AtomicBoolean isCancelled, CopyProgressListener listener, CopyResultCallback callback) {
        executor.submit(() -> {
            boolean result;
            long totalCopied = copyOrMove(source, dest, isMove, isCancelled, listener, totalSize);

            // Cancel cleanup
            if (isCancelled.get() && dest.exists()) {
                deleteRecursive(dest);
                result = false;
            } else {
                result = true;
            }

            boolean finalResult = result;
            long fileCopiedSize = totalCopied;
            mainHandler.post(() -> callback.onComplete(finalResult, fileCopiedSize));
        });
    }

    private static long copyOrMove(File source, File dest, boolean isMove, AtomicBoolean isCancelled, CopyProgressListener listener, long totalSize) {

        if (isCancelled.get()) return 0;

        long copiedSize = 0;

        if (source.isDirectory()) {
            copiedSize += copyOrMoveFolder(source, dest, isMove, isCancelled, listener, totalSize, copiedSize);
        } else {
            copiedSize += copyOrMoveFile(source, dest, isMove, isCancelled, listener, totalSize, copiedSize);
        }

        return copiedSize;
    }

    private static long copyOrMoveFolder(File source, File dest, boolean isMove, AtomicBoolean isCancelled, CopyProgressListener listener, long totalSize, long copiedSoFar) {

        if (isCancelled.get()) return copiedSoFar;

        long copiedInThisFolder = 0;

        // Fast move try (rename)
        if (isMove && source.renameTo(dest)) {
            copiedInThisFolder = FileUtility.getTotalSize(dest); // Folder total size after move
            updateListener(listener, copiedSoFar + copiedInThisFolder, totalSize, source.getName());
            return copiedInThisFolder;
        }

        if (!dest.exists()) dest.mkdirs();

        File[] files = source.listFiles();
        if (files != null) {
            for (File file : files) {
                if (isCancelled.get()) return copiedSoFar;

                File newDest = new File(dest, file.getName());

                if (file.isDirectory()) {
                    // Recursive call for subfolder
                    copiedInThisFolder += copyOrMoveFolder(file, newDest, isMove, isCancelled, listener, totalSize, copiedSoFar + copiedInThisFolder);
                } else {
                    // Copy single file
                    copiedInThisFolder += copyOrMoveFile(file, newDest, isMove, isCancelled, listener, totalSize, copiedSoFar + copiedInThisFolder);
                }

                if (isCancelled.get()) {
                    deleteRecursive(dest);
                    return copiedSoFar;
                }
            }
        }

        if (isMove) deleteRecursive(source);

        return copiedInThisFolder;
    }

    private static long copyOrMoveFile(File source, File dest, boolean isMove, AtomicBoolean isCancelled, CopyProgressListener listener, long totalSize, long copiedSoFar) {

        if (isCancelled.get()) return 0;

        long fileCopied = 0;

        // Fast move try
        if (isMove && source.renameTo(dest)) {
            fileCopied = FileUtility.getTotalSize(dest); // Folder total size after move
            updateListener(listener, copiedSoFar + fileCopied, totalSize, source.getName());
            return fileCopied;
        }

        try (FileInputStream in = new FileInputStream(source); FileOutputStream out = new FileOutputStream(dest)) {

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
                    return fileCopied;
                }

                out.write(buffer, 0, length);
                fileCopied += length;

                long now = System.currentTimeMillis();
                if (now - lastUpdate > 500) {
                    updateListener(listener, copiedSoFar + fileCopied, totalSize, source.getName());
                    lastUpdate = now;
                }
            }
            out.flush();

            updateListener(listener, copiedSoFar + fileCopied, totalSize, source.getName());

        } catch (IOException e) {
            e.printStackTrace();
            if (dest.exists()) dest.delete();
            return 0;
        }

        if (isMove) source.delete();
        return fileCopied;
    }

    private static void updateListener(CopyProgressListener listener, long copied, long totalSize, String name) {
        if (listener != null) {
            mainHandler.post(() -> listener.onProgress(copied, totalSize, name));
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
        void onComplete(boolean success, long fileCopiedSize);
    }
}