package com.photobackup;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class PhotoBackupApp extends Application {
    private static final String TAG = "PhotoBackupApp";
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Log.d(TAG, "Application created");
    }

    public static Context getContext() {
        return mContext;
    }
}