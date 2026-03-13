package com.photobackup;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.photobackup.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 100;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkPermissions();
        initUI();
    }

    private void checkPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();
        String[] requiredPermissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE
        };

        for (String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission);
            }
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsNeeded.toArray(new String[0]),
                    PERMISSION_REQUEST_CODE
            );
        }
    }

    private void initUI() {
        binding.btnConnect.setOnClickListener(v -> connectToSamba());
        binding.btnBackup.setOnClickListener(v -> backupPhotos());
        binding.btnBrowse.setOnClickListener(v -> browsePhotos());
    }

    private void connectToSamba() {
        String server = binding.etServer.getText().toString().trim();
        String share = binding.etShare.getText().toString().trim();
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (server.isEmpty() || share.isEmpty()) {
            showToast("请输入服务器和共享名");
            return;
        }

        new Thread(() -> {
            boolean connected = SambaManager.connect(
                    server,
                    share,
                    username,
                    password
            );

            runOnUiThread(() -> {
                if (connected) {
                    showToast("连接成功");
                    binding.btnBackup.setEnabled(true);
                    binding.btnBrowse.setEnabled(true);
                } else {
                    showToast("连接失败，请检查参数");
                }
            });
        }).start();
    }

    private void backupPhotos() {
        new Thread(() -> {
            boolean success = PhotoBackupManager.backupPhotos();
            runOnUiThread(() -> {
                if (success) {
                    showToast("备份成功");
                } else {
                    showToast("备份失败");
                }
            });
        }).start();
    }

    private void browsePhotos() {
        // 实现图片浏览功能
        showToast("图片浏览功能开发中");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (!allGranted) {
                showToast("权限被拒绝，应用功能可能受限");
            }
        }
    }

    @Override
    protected void onDestroy() {
        SambaManager.disconnect();
        super.onDestroy();
    }
}