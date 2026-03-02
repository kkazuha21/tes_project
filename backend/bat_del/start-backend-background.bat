@echo off
chcp 65001 >nul
echo ============================================
echo 共享实验室平台 - 后端后台启动
echo ============================================
echo.

set "JAVA_HOME=C:\Users\xzy20\jdk-21"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo 当前使用 Java 版本：
java -version
echo.
echo JAVA_HOME: %JAVA_HOME%
echo.

echo ============================================
echo 正在以后台模式启动后端服务...
echo ============================================
echo.

cd /d "%~dp0"

:: 使用 start 命令在新窗口中运行
start "SharedLab Backend" cmd /c "C:\Users\xzy20\.m2\wrapper\dists\apache-maven-3.9.5-bin\32db9c34\apache-maven-3.9.5\bin\mvn.cmd -f pom.xml spring-boot:run -DskipTests"

echo.
echo ============================================
echo 后端正在新窗口中启动...
echo 请查看新打开的窗口以查看启动日志
echo 后端将运行在: http://localhost:8080
echo ============================================
echo.
pause
