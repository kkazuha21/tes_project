# 包结构修复说明

## 问题描述

创建的 Java 文件被放置在了错误的目录结构中：

**错误的结构**：
```
backend/
  └── src/
      └── main/
          └── java/
              └── main/          ❌ 多余的层级
                  └── java/      ❌ 多余的层级
                      └── com/
                          └── sharedlab/
```

**正确的结构**：
```
backend/
  └── src/
      └── main/
          └── java/
              └── com/           ✅ 直接在 java 目录下
                  └── sharedlab/
```

## 解决方案

### 方法1: 使用修复脚本（推荐）

#### Windows
```bash
fix-package-structure.bat
```

#### Linux/Mac
```bash
chmod +x fix-package-structure.sh
./fix-package-structure.sh
```

### 方法2: 手动修复

1. 打开文件管理器，导航到：
   ```
   C:\SharedLab\backend\src\main\java\
   ```

2. 如果看到 `main/java/com/sharedlab/` 的嵌套结构：
   - 将 `main/java/com/sharedlab/` 下的所有文件
   - 移动到 `com/sharedlab/` 目录（直接在 java 目录下创建）
   - 删除多余的 `main` 文件夹

3. 最终结构应该是：
   ```
   backend/src/main/java/com/sharedlab/
   ├── config/
   ├── controller/
   ├── model/
   ├── repository/
   ├── service/
   ├── security/
   ├── exception/
   └── SharedLabApplication.java
   ```

### 方法3: 在 VS Code 中手动移动

1. 在 VS Code 左侧文件树中
2. 找到 `backend/src/main/java/main/java/com/sharedlab/`
3. 将 `com` 文件夹拖动到 `backend/src/main/java/` 下
4. 删除空的 `main` 文件夹

## 验证修复

运行以下命令检查包结构：

### Windows
```bash
cd backend\src\main\java
dir /S /B *.java
```

### Linux/Mac
```bash
cd backend/src/main/java
find . -name "*.java"
```

应该看到类似输出：
```
./com/sharedlab/SharedLabApplication.java
./com/sharedlab/config/SecurityConfig.java
./com/sharedlab/config/WebConfig.java
./com/sharedlab/controller/AuthController.java
...
```

## 为什么会出现这个问题？

创建文件时，路径被错误地包含了 `main/java/` 前缀，导致：
- 输入路径：`C:\SharedLab\backend\src\main\java\com\sharedlab\...`
- 实际创建：`C:\SharedLab\backend\src\main\java\main\java\com\sharedlab\...`

## 修复后需要做什么？

1. **刷新 IDE**：重新加载项目或重启 VS Code
2. **Maven 重新导入**：在 IDE 中重新导入 Maven 项目
3. **验证编译**：
   ```bash
   cd backend
   mvn clean compile
   ```

## 预防此问题

在创建新文件时，确保使用正确的包路径：
- ✅ `package com.sharedlab.xxx;`
- ❌ `package main.java.com.sharedlab.xxx;`
