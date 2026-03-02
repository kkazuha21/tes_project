@echo off
chcp 65001 >nul
echo ====================================
echo Java 环境检查
echo ====================================
echo.

echo 检查 Java 版本...
java -version
echo.

echo 检查 JAVA_HOME...
if defined JAVA_HOME (
    echo JAVA_HOME = %JAVA_HOME%
    echo.
    echo Java 可执行文件:
    "%JAVA_HOME%\bin\java.exe" -version
) else (
    echo [警告] JAVA_HOME 未设置
)
echo.

echo 检查 javac 版本...
javac -version
echo.

echo ====================================
echo 诊断信息
echo ====================================
echo.

java -version 2>&1 | findstr "version" > temp.txt
set /p JAVA_VER=<temp.txt
del temp.txt

echo 检测到: %JAVA_VER%
echo.

echo 项目要求: Java 17 或更高版本
echo.

java -version 2>&1 | findstr "17\|18\|19\|20\|21" >nul
if %errorlevel% equ 0 (
    echo [✓] Java 版本满足要求
) else (
    echo [✗] Java 版本不满足要求
    echo.
    echo 请安装 Java 17 或更高版本
    echo 下载地址: https://www.oracle.com/java/technologies/downloads/
)

echo.
pause
