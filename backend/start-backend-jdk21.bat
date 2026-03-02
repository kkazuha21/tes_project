@echo off
REM 使用 JDK 21 + 已下载的 Maven 运行后端
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
echo 正在启动后端服务（清理并运行）...
echo ============================================
echo.

"C:\Users\xzy20\.m2\wrapper\dists\apache-maven-3.9.5-bin\32db9c34\apache-maven-3.9.5\bin\mvn.cmd" -f "%~dp0pom.xml" clean spring-boot:run -DskipTests

pause
