@echo off
REM 使用 JDK 21 运行后端（临时环境变量，不修改系统配置）
setlocal
chcp 65001 >nul 2>&1
set "JAVA_HOME=C:\Users\xzy20\jdk-21"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo ============================================
echo 共享实验室平台 - 后端启动脚本
echo ============================================
echo.
echo 当前使用 Java 版本：
java -version
echo.
echo JAVA_HOME: %JAVA_HOME%
echo.
echo ============================================
echo 正在启动后端服务...
echo ============================================
echo.

mvnw.cmd clean spring-boot:run -DskipTests

pause
