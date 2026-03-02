@echo off
chcp 65001 >nul
echo ====================================
echo 共享Lab - 环境修复脚本
echo ====================================
echo.

echo [1/3] 修复前端环境...
cd frontend

if exist "node_modules" (
    echo 清理旧的 node_modules...
    rmdir /s /q node_modules
)

if exist "package-lock.json" (
    del package-lock.json
)

echo 重新安装前端依赖...
call npm install
if %errorlevel% neq 0 (
    echo [错误] 前端依赖安装失败
    cd ..
    pause
    exit /b 1
)

echo [✓] 前端环境修复完成
cd ..
echo.

echo [2/3] 修复后端 Maven Wrapper...
cd backend

REM 删除旧的 wrapper 文件
if exist ".mvn\wrapper\maven-wrapper.jar" (
    del .mvn\wrapper\maven-wrapper.jar
)

REM 创建目录
if not exist ".mvn\wrapper" (
    mkdir .mvn\wrapper
)

echo 正在下载 Maven Wrapper...
powershell -ExecutionPolicy Bypass -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar' -OutFile '.mvn/wrapper/maven-wrapper.jar' -UseBasicParsing}"

if not exist ".mvn\wrapper\maven-wrapper.jar" (
    echo [警告] Maven Wrapper 下载失败
    echo 将尝试使用系统 Maven 运行
) else (
    echo [✓] Maven Wrapper 下载成功
)

cd ..
echo.

echo [3/3] 验证环境...
echo.

echo 检查前端 Vite:
cd frontend
call npm list vite
cd ..
echo.

echo 检查 Maven:
where mvn >nul 2>&1
if %errorlevel% equ 0 (
    echo [✓] 系统 Maven 可用
    mvn -version
) else (
    echo [!] 系统 Maven 不可用，将使用 Maven Wrapper
)
echo.

echo ====================================
echo 环境修复完成！
echo ====================================
echo.
echo 现在可以启动项目了
echo 请运行: start-fixed.bat
echo.
pause
