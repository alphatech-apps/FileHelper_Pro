package com.jakir.filehelper;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class FileUtility {
    private FileUtility() {
    }

    public static String getTotalItemsInsideFolder(File folder) {
        if (folder == null || !folder.exists() || !folder.isDirectory()){
            return "Empty folder";
        }

        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            return "Empty folder";
        }

        int count = files.length;
        return count + (count == 1 ? " item" : " items");
    }


    public static long getTotalSize(File file) {
        if (file.isFile()) return file.length();

        long total = 0;
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) total += getTotalSize(child);
        }
        return total;
    }

    public static void getTotalFileFolder(List<File> selectedFiles, FileFolderCallback callback) {
        new Thread(() -> {
            AtomicInteger folderCount = new AtomicInteger(0);
            AtomicInteger fileCount = new AtomicInteger(0);

            for (File f : selectedFiles) {
                calculateFileFolder(f, folderCount, fileCount);
            }

            if (callback != null) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onFileFolderCalculated(folderCount.get(), fileCount.get()));
            }
        }).start();
    }

    private static void calculateFileFolder(File file, AtomicInteger folderCount, AtomicInteger fileCount) {
        if (file.isDirectory()) {
            folderCount.incrementAndGet();
      /*      File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    calculateFileFolder(child, folderCount, fileCount);
                }
            }*/
        } else {
            fileCount.incrementAndGet();
        }
    }

    public static void getTotalFileFolderSize(List<File> selectedFiles, FileFolderSizeCallback callback) {
        new Thread(() -> {
            AtomicInteger folderCount = new AtomicInteger(0);
            AtomicInteger fileCount = new AtomicInteger(0);
            AtomicLong totalSize = new AtomicLong(0);

            for (File f : selectedFiles) {
                calculateFolderFileDetails(f, folderCount, fileCount, totalSize);
            }

            if (callback != null) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onDetailsCalculated(folderCount.get(), fileCount.get(), totalSize.get()));
            }
        }).start();
    }

    private static void calculateFolderFileDetails(File file, AtomicInteger folderCount, AtomicInteger fileCount, AtomicLong totalSize) {
        if (file.isDirectory()) {
            folderCount.incrementAndGet();
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    calculateFolderFileDetails(child, folderCount, fileCount, totalSize);
                }
            }
        } else {
            fileCount.incrementAndGet();
            totalSize.addAndGet(file.length());
        }
    }

    public static String humanReadableSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format(Locale.getDefault(), "%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    // Copy single file with cancel support
    public static void copyFile(File sourceFile, File destFile, AtomicBoolean isCancelled) throws IOException {
        if (!sourceFile.exists()) return;

        try (InputStream in = new FileInputStream(sourceFile); OutputStream out = new FileOutputStream(destFile)) {

            // Auto buffer size depending on file size
            long fileSize = sourceFile.length();
            int bufferSize;

            if (fileSize < 10 * 1024 * 1024) { // < 10MB
                bufferSize = 8 * 1024; // 8KB
            } else if (fileSize < 100 * 1024 * 1024) { // 10MB - 100MB
                bufferSize = 16 * 1024; // 16KB
            } else {
                bufferSize = 32 * 1024; // 32KB
            }

            byte[] buffer = new byte[bufferSize];
            int length;

            while ((length = in.read(buffer)) > 0) {
                if (isCancelled != null && isCancelled.get()) {
                    // Cancel pressed → delete partial file
                    out.close();
                    destFile.delete();
                    return;
                }
                out.write(buffer, 0, length);
            }
        }
    }

    // Copy directory recursive with cancel + auto delete
    public static void copyDirectory(File sourceDir, File destDir, AtomicBoolean isCancelled) throws IOException {
        if (isCancelled.get()) return;

        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        String[] children = sourceDir.list();
        if (children == null) return;

        for (String child : children) {
            if (isCancelled.get()) {
                // cancel হলে empty/incomplete folder delete
                if (destDir.exists() && destDir.list().length == 0) {
                    destDir.delete();
                }
                return;
            }

            File srcFile = new File(sourceDir, child);
            File destFile = new File(destDir, child);

            if (srcFile.isDirectory()) {
                copyDirectory(srcFile, destFile, isCancelled);
            } else {
                copyFile(srcFile, destFile, isCancelled);
            }
        }
    }


    public interface FileFolderSizeCallback {
        void onDetailsCalculated(int folderCount, int fileCount, long totalSize);
    }

    public interface FileFolderCallback {
        void onFileFolderCalculated(int folderCount, int fileCount);
    }
}

