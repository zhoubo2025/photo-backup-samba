# Photo Backup Android App

一个简单易用的 Android 应用，用于将设备照片备份到 Samba 共享存储。

## 功能特性

### 📸 照片备份
- 自动扫描设备中的照片
- 支持增量备份，避免重复备份
- 可配置备份位置和文件格式

### 🔗 Samba 连接
- 支持 SMB v2/v3 协议
- 自动保存连接配置
- 连接失败自动重试机制

### 📱 用户界面
- 现代化 Material Design 风格
- 响应式布局，适配不同屏幕尺寸
- 实时备份进度显示

### ⚡ 性能优化
- 后台服务执行备份操作
- 智能压缩和缓存策略
- 低电量和网络状态检测

## 技术架构

### 核心模块

#### `PhotoBackupApp.java`
- 应用程序入口点
- 全局状态管理
- 初始化和资源配置

#### `MainActivity.java`
- 主界面控制器
- 用户交互处理
- 权限管理

#### `SambaManager.java`
- Samba 连接管理
- 网络请求处理
- 错误恢复机制

#### `PhotoBackupManager.java`
- 照片扫描和识别
- 备份策略管理
- 进度跟踪和报告

#### `FileUtils.java`
- 文件操作工具类
- 目录创建和权限处理
- 文件复制和校验

#### `BackupService.java`
- 后台服务实现
- 通知显示和管理
- 长时间运行任务支持

### 依赖库

```gradle
// Android X
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.appcompat:appcompat:1.6.1")
implementation("com.google.android.material:material:1.11.0")

// Networking
implementation("org.samba.jcifs:jcifs-ng:2.11.1")

// Lifecycle
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// Image Loading
implementation("com.github.bumptech.glide:glide:4.16.0")

// Dagger Hilt
implementation("com.google.dagger:hilt-android:2.48")
ksp("com.google.dagger:hilt-android-compiler:2.48")
```

## 构建说明

### 环境要求

- Android SDK 34+
- JDK 17+
- Gradle 8.1+

### 构建命令

```bash
# 构建 Debug 版本
./gradlew assembleDebug

# 构建 Release 版本
./gradlew assembleRelease

# 运行单元测试
./gradlew testDebugUnitTest

# 运行仪器化测试
./gradlew connectedDebugAndroidTest
```

### GitHub Actions

项目已配置自动构建工作流程：

1. 推送代码到 `main` 分支
2. GitHub Actions 自动运行构建
3. 成功后在 "Actions" 标签页下载 APK

## 使用说明

### 首次使用

1. 安装应用并启动
2. 配置 Samba 连接信息：
   - 服务器地址（例如：192.168.1.100）
   - 共享名（例如：photo_backup）
   - 用户名（可选）
   - 密码（可选）
3. 点击 "连接 Samba" 按钮
4. 连接成功后，点击 "备份照片"

### 配置选项

#### 备份设置
- **备份路径**：Samba 共享上的备份位置
- **文件格式**：支持 JPEG、PNG、WebP
- **备份频率**：自动备份间隔
- **网络限制**：仅在 WiFi 下备份

#### 通知设置
- **备份进度**：显示实时进度
- **完成通知**：备份完成后通知
- **失败提醒**：备份失败时提示

## 项目结构

```
photo-backup-samba/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/photobackup/
│   │   │   │   ├── PhotoBackupApp.java      # 应用入口
│   │   │   │   ├── MainActivity.java         # 主界面
│   │   │   │   ├── SambaManager.java         # Samba 连接管理
│   │   │   │   ├── PhotoBackupManager.java   # 照片备份管理
│   │   │   │   ├── FileUtils.java             # 文件操作工具
│   │   │   │   └── BackupService.java         # 后台服务
│   │   │   └── res/
│   │   │       ├── layout/                    # 界面布局
│   │   │       ├── menu/                      # 菜单资源
│   │   │       ├── drawable/                  # 图形资源
│   │   │       └── values/                    # 字符串和样式
│   │   └── androidTest/
│   ├── build.gradle.kts                       # 应用依赖配置
│   └── proguard-rules.pro                     # 混淆规则
├── build.gradle.kts                           # 项目依赖配置
├── settings.gradle.kts                        # 项目设置
└── gradle.properties                          # 全局属性
```

## 安全说明

### 数据安全

- 所有网络通信使用安全协议
- 密码和敏感信息加密存储
- 文件传输时进行完整性校验

### 权限要求

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.VIBRATE" />
```

## 开发计划

### 已完成

✅ 基础 Samba 连接和照片备份
✅ 后台服务和通知支持
✅ 权限管理和用户界面
✅ 错误处理和恢复机制

### 待实现

- [ ] 增量备份优化
- [ ] 多备份位置支持
- [ ] 文件压缩和加密
- [ ] 云存储集成（Dropbox、Google Drive）
- [ ] 照片预览和选择功能
- [ ] 定时备份任务
- [ ] 备份报告和分析

## 问题反馈

如果您遇到任何问题或有改进建议，请通过以下方式联系：

1. **Issue Tracker**：https://github.com/your-username/photo-backup-samba/issues
2. **Email**：support@photobackup.app
3. **Wiki**：https://github.com/your-username/photo-backup-samba/wiki

## 许可证

本项目使用 MIT 许可证：

```
MIT License

Copyright (c) 2024 Photo Backup Team

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```