package com.photobackup;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BackupService extends Service {
    private static final String TAG = "BackupService";
    private static final String CHANNEL_ID = "PHOTO_BACKUP_CHANNEL";
    private static final int NOTIFICATION_ID = 100;
    private static final int NOTIFICATION_PROGRESS_ID = 101;

    private boolean isRunning = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRunning) {
            isRunning = true;
            startBackup();
        }
        return START_STICKY;
    }

    private void startBackup() {
        showNotification("照片备份", "正在扫描照片...");

        new Thread(() -> {
            try {
                boolean success = PhotoBackupManager.backupPhotos();
                updateNotification("照片备份", success ? "备份成功" : "备份失败");

                if (success) {
                    String backupTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            .format(new Date());
                    Log.d(TAG, "Backup completed at: " + backupTime);
                }
            } catch (Exception e) {
                Log.e(TAG, "Backup failed: " + e.getMessage(), e);
                updateNotification("照片备份", "备份失败");
            } finally {
                isRunning = false;
                stopSelf();
            }
        }).start();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "照片备份";
            String description = "照片备份服务通知";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true);

        Notification notification = builder.build();
        startForeground(NOTIFICATION_ID, notification);
    }

    private void updateNotification(String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(false);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void showProgressNotification(String title, String content, int progress) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true)
                .setProgress(100, progress, false);

        Notification notification = builder.build();
        startForeground(NOTIFICATION_PROGRESS_ID, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
        isRunning = false;
    }
}