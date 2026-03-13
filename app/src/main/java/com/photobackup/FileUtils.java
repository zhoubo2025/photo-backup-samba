package com.photobackup;

import android.util.Log;

import jcifs.smb.SmbFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    private static final String TAG = "FileUtils";
    private static final int BUFFER_SIZE = 8192;

    public static boolean copyFile(File localFile, SmbFile remoteFile) {
        if (!localFile.exists()) {
            Log.e(TAG, "Local file does not exist: " + localFile.getAbsolutePath());
            return false;
        }

        try (InputStream in = new BufferedInputStream(new FileInputStream(localFile));
             OutputStream out = new BufferedOutputStream(new SmbFileOutputStream(remoteFile))) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            out.flush();
            Log.d(TAG, "File copied successfully: " + remoteFile.getPath());
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error copying file: " + e.getMessage(), e);
            return false;
        }
    }

    public static boolean copySmbToLocal(SmbFile remoteFile, File localFile) {
        try (InputStream in = new BufferedInputStream(new SmbFileInputStream(remoteFile));
             OutputStream out = new BufferedOutputStream(new FileOutputStream(localFile))) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            out.flush();
            Log.d(TAG, "File copied to local: " + localFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error copying file to local: " + e.getMessage(), e);
            return false;
        }
    }

    public static long getFileSize(File file) {
        return file.length();
    }

    public static String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }

    public static boolean isImageFile(File file) {
        String extension = getFileExtension(file);
        return extension.equals("jpg") || extension.equals("jpeg") ||
               extension.equals("png") || extension.equals("gif") ||
               extension.equals("bmp") || extension.equals("webp");
    }

    public static String getFileNameWithoutExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return fileName.substring(0, dotIndex);
        }
        return fileName;
    }

    public static String getFileSizeReadable(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }

    public static boolean createDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                Log.d(TAG, "Directory created: " + dirPath);
            } else {
                Log.e(TAG, "Failed to create directory: " + dirPath);
            }
            return created;
        }
        return true;
    }

    public static boolean deleteFile(File file) {
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                Log.d(TAG, "File deleted: " + file.getAbsolutePath());
            } else {
                Log.e(TAG, "Failed to delete file: " + file.getAbsolutePath());
            }
            return deleted;
        }
        return true;
    }

    public static List<File> listFiles(String directoryPath, String extension) {
        List<File> result = new ArrayList<>();
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && (extension == null ||
                        file.getName().toLowerCase().endsWith(extension.toLowerCase()))) {
                        result.add(file);
                    }
                }
            }
        }

        return result;
    }
}