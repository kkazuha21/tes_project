@echo off
chcp 65001 >nul
echo ====================================
echo 共享Lab - 使用 IntelliJ IDEA 启动指南
echo ====================================
echo.
echo 检测到系统未安装 Maven
echo.
echo 推荐使用 IntelliJ IDEA 运行项目（最简单的方式）
echo.
echo ============================================================
echo 步骤 1: 下载并安装 IntelliJ IDEA Community Edition（免费）
echo ============================================================
echo.
echo 下载地址: https://www.jetbrains.com/idea/download/
echo 选择: Community Edition (免费版本)
echo.
pause
echo.
echo ============================================================
echo 步骤 2: 用 IntelliJ IDEA 打开项目
echo ============================================================
echo.
echo 1. 启动 IntelliJ IDEA
echo 2. 点击 "Open" 或 "Open Project"
echo 3. 选择文件夹: C:\SharedLab\backend
echo 4. 点击 "OK"
echo 5. 等待 IDE 自动下载依赖（首次需要 5-10 分钟）
echo.
pause
echo.
echo ============================================================
echo 步骤 3: 配置数据库连接
echo ============================================================
echo.
echo 1. 在 IDE 中打开: src/main/resources/application.properties
echo 2. 修改以下配置:
echo    spring.datasource.username=root
echo    spring.datasource.password=你的MySQL密码
echo 3. 保存文件 (Ctrl+S)
echo.
pause
echo.
echo ============================================================
echo 步骤 4: 运行后端
echo ============================================================
echo.
echo 1. 在 IDE 左侧找到: src/main/java/com/sharedlab/SharedLabApplication.java
echo 2. 右键点击文件
echo 3. 选择 "Run 'SharedLabApplication'"
echo 4. 看到 "Started SharedLabApplication" 表示启动成功
echo.
pause
echo.
echo ============================================================
echo 步骤 5: 启动前端
echo ============================================================
echo.
echo 前端已经在运行: http://localhost:5173
echo 如果没有运行，打开新的命令行窗口：
echo   cd C:\SharedLab\frontend
echo   npm run dev
echo.
echo ============================================================
echo 完成！
echo ============================================================
echo.
echo 访问: http://localhost:5173
echo.
echo 测试账号:
echo   用户名: admin
echo   密码: password
echo.
echo 注意: 首次启动需要确保 MySQL 数据库已启动并导入了初始数据
echo       (运行 database/init.sql)
echo.
pause
