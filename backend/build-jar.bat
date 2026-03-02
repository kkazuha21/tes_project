@echo off
REM 构建 fat-jar 包
setlocal
chcp 65001 >nul 2>&1
set "JAVA_HOME=C:\Users\xzy20\jdk-21"
set "PATH=%JAVA_HOME%\bin;%PATH%"

cd /d "%~dp0"

echo ============================================
echo 构建后端 fat-jar 包
echo ============================================
echo.
echo 当前使用 Java 版本：
java -version
echo.
echo ============================================
echo 正在构建...
echo ============================================
echo.

REM 清理并打包
mvn clean package -DskipTests

echo.
echo ============================================
echo 构建完成！
echo JAR 文件位置：target\shared-lab-platform-1.0.0.jar
echo ============================================
pause
