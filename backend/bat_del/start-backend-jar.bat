@echo off
chcp 65001 >nul
echo ============================================
echo 共享实验室平台 - 后端启动 (JAR模式)
echo ============================================
echo.

set "JAVA_HOME=C:\Users\xzy20\jdk-21"
set "PATH=%JAVA_HOME%\bin;%PATH%"

cd /d "%~dp0"

echo 第一步: 打包应用...
call C:\Users\xzy20\.m2\wrapper\dists\apache-maven-3.9.5-bin\32db9c34\apache-maven-3.9.5\bin\mvn.cmd clean package -DskipTests

if exist "target\shared-lab-platform-1.0.0.jar" (
    echo.
    echo ============================================
    echo 第二步: 启动后端服务...
    echo ============================================
    java -jar target\shared-lab-platform-1.0.0.jar
) else (
    echo.
    echo 错误: JAR 文件未找到!
    pause
)
