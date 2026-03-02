@echo off
chcp 65001 >nul
echo ====================================
echo 共享Lab - 后端启动（使用 Maven Wrapper）
echo ====================================
echo.

cd /d C:\SharedLab\backend

echo 检查 Maven Wrapper...
if exist "mvnw.cmd" (
    echo Maven Wrapper 已找到
    echo.
    echo 正在清理并启动后端...
    echo 这可能需要几分钟（首次启动会下载依赖）
    echo.
    
    .\mvnw.cmd clean spring-boot:run -Dmaven.test.skip=true
    
) else (
    echo.
    echo 错误: Maven Wrapper 未找到！
    echo.
    echo 解决方案：
    echo 1. 推荐方式：使用 IntelliJ IDEA 运行
    echo    运行: IDEA-启动指南.bat 查看详细步骤
    echo.
    echo 2. 或者手动安装 Maven：
    echo    - 下载: https://maven.apache.org/download.cgi
    echo    - 解压到: C:\maven
    echo    - 添加到环境变量 PATH: C:\maven\bin
    echo    - 重启命令行窗口
    echo.
    pause
)

pause
