package com.photobackup;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PhotoBackupManager {
    private static final String TAG = "PhotoBackupManager";
    private static final String BACKUP_DIR = "photo_backup";
    private static final int MAX_BACKUP_RETRY = 3;

    public static boolean backupPhotos() {
        if (!SambaManager.isConnected()) {
            Log.e(TAG, "Samba not connected");
            return false;
        }

        List<File> photos = getLocalPhotos();
        if (photos.isEmpty()) {
            Log.d(TAG, "No photos found to backup");
            return true;
        }

        String remoteBackupDir = BACKUP_DIR + "/" + getCurrentDate();
        int successCount = 0;
        int failedCount = 0;

        for (File photo : photos) {
            boolean backupSuccess = false;
            int retryCount = 0;

            while (retryCount < MAX_BACKUP_RETRY && !backupSuccess) {
                backupSuccess = SambaManager.backupFile(photo, remoteBackupDir);
                retryCount++;

                if (!backupSuccess) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Retry interrupted: " + e.getMessage());
                    }
                }
            }

            if (backupSuccess) {
                successCount++;
            } else {
                failedCount++;
            }
        }

        Log.d(TAG, "Backup completed: " + successCount + " successful, " + failedCount + " failed");
        return failedCount == 0;
    }

    private static List<File> getLocalPhotos() {
        List<File> photos = new ArrayList<>();
        ContentResolver resolver = PhotoBackupApp.getContext().getContentResolver();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media.MIME_TYPE + " LIKE ?";
        String[] selectionArgs = new String[]{"%image%"};
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        try (Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, sortOrder)) {
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                while (cursor.moveToNext()) {
                    String path = cursor.getString(columnIndex);
                    File file = new File(path);
                    if (file.exists() && file.length() > 0) {
                        photos.add(file);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to retrieve photos: " + e.getMessage(), e);
        }

        return photos;
    }

    private static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    public static List<File> getBackupStatus() {
        List<File> backedUpPhotos = new ArrayList<>();
        if (!SambaManager.isConnected()) {
            Log.e(TAG, "Samba not connected");
            return backedUpPhotos;
        }

        String remoteBackupDir = BACKUP_DIR + "/" + getCurrentDate();
        String[] files = SambaManager.listDirectory(remoteBackupDir);

        if (files != null) {
            for (String fileName : files) {
                if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg") ||
                        fileName.toLowerCase().endsWith(".png") || fileName.toLowerCase().endsWith(".gif")) {
                    backedUpPhotos.add(new File(fileName));
                }
            }
        }

        return backedUpPhotos;
    }

    public static int getTotalPhotos() {
        return getLocalPhotos().size();
    }

    public static int getBackedUpCount() {
        return getBackupStatus().size();
    }

    public static void cleanOldBackups() {
        // 实现自动清理旧备份功能
        Log.d(TAG, "Cleaning old backups not implemented yet");
    }
}