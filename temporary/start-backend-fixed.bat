@echo off
chcp 65001 >nul
echo ====================================
echo 共享Lab - 后端启动（修复版）
echo ====================================
echo.

cd /d C:\SharedLab\backend

echo 设置 Maven 环境变量...
set MAVEN_PROJECTBASEDIR=%CD%

echo 正在清理并启动后端...
echo 这可能需要几分钟（首次启动会下载依赖）
echo.

mvnw.cmd clean spring-boot:run -Dmaven.test.skip=true

if errorlevel 1 (
    echo.
    echo ====================================
    echo 启动失败！
    echo ====================================
    echo.
    echo 最简单的解决方案：
    echo 使用 IntelliJ IDEA 运行项目
    echo.
    echo 请运行: IDEA-启动指南.bat
    echo.
    pause
    exit /b 1
)

pause
