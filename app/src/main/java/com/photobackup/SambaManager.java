package com.photobackup;

import android.util.Log;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

import java.io.File;
import java.io.IOException;

public class SambaManager {
    private static final String TAG = "SambaManager";
    private static String sambaPath;
    private static boolean connected = false;
    private static NtlmPasswordAuthentication auth;

    public static boolean connect(String server, String share, String username, String password) {
        try {
            String url = "smb://" + server + "/" + share;
            sambaPath = url;

            if (username != null && !username.isEmpty()) {
                auth = new NtlmPasswordAuthentication("", username, password);
            } else {
                auth = null;
            }

            SmbFile smbFile = new SmbFile(url, auth);
            if (smbFile.exists() && smbFile.isDirectory()) {
                connected = true;
                Log.d(TAG, "Connected to " + url);
                return true;
            } else {
                Log.e(TAG, "Samba share not found or not a directory: " + url);
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Samba connection failed: " + e.getMessage(), e);
            return false;
        }
    }

    public static boolean disconnect() {
        if (connected) {
            connected = false;
            sambaPath = null;
            auth = null;
            Log.d(TAG, "Disconnected from Samba");
            return true;
        }
        return false;
    }

    public static boolean isConnected() {
        return connected;
    }

    public static String getSambaPath() {
        return sambaPath;
    }

    public static NtlmPasswordAuthentication getAuth() {
        return auth;
    }

    public static boolean backupFile(File localFile, String remoteDir) {
        if (!connected) {
            Log.e(TAG, "Samba not connected");
            return false;
        }

        try {
            String remotePath = remoteDir.endsWith("/") ? remoteDir : remoteDir + "/";
            String remoteFileUrl = sambaPath + "/" + remotePath + localFile.getName();

            SmbFile remoteFile = new SmbFile(remoteFileUrl, auth);
            if (!remoteFile.getParentFile().exists()) {
                remoteFile.getParentFile().mkdirs();
            }

            FileUtils.copyFile(localFile, remoteFile);
            Log.d(TAG, "File backed up to Samba: " + remoteFileUrl);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Backup file failed: " + e.getMessage(), e);
            return false;
        }
    }

    public static String[] listDirectory(String remoteDir) {
        if (!connected) {
            Log.e(TAG, "Samba not connected");
            return null;
        }

        try {
            String remotePath = remoteDir.endsWith("/") ? remoteDir : remoteDir + "/";
            String remoteUrl = sambaPath + "/" + remotePath;
            SmbFile dir = new SmbFile(remoteUrl, auth);

            if (dir.exists() && dir.isDirectory()) {
                return dir.list();
            } else {
                Log.e(TAG, "Directory not found: " + remoteUrl);
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "List directory failed: " + e.getMessage(), e);
            return null;
        }
    }
}