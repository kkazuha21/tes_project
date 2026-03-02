@echo off
chcp 65001 >nul
echo ====================================
echo 共享Lab - 快速启动（无需Maven）
echo ====================================
echo.

echo [1/3] 检查环境...
echo.

REM 检查 Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到Java，请先安装Java 17+
    echo 下载地址: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)
echo [✓] Java 已安装

REM 检查 Node.js
node -v >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到Node.js，请先安装Node.js 16+
    echo 下载地址: https://nodejs.org/
    pause
    exit /b 1
)
echo [✓] Node.js 已安装
echo.

echo [2/3] 安装前端依赖...
cd frontend
if not exist "node_modules" (
    call npm install
    if %errorlevel% neq 0 (
        echo [错误] 前端依赖安装失败
        cd ..
        pause
        exit /b 1
    )
)
cd ..
echo [✓] 前端依赖已安装
echo.

echo [3/3] 启动服务...
echo.
echo 正在启动后端服务 (端口 8080)...
echo 首次启动会自动下载Maven依赖，请耐心等待...
cd backend
start "SharedLab Backend" cmd /k "mvnw.cmd spring-boot:run"
cd ..

timeout /t 10 /nobreak >nul

echo 正在启动前端服务 (端口 5173)...
cd frontend
start "SharedLab Frontend" cmd /k "npm run dev"
cd ..

echo.
echo ====================================
echo 服务启动成功！
echo ====================================
echo 后端地址: http://localhost:8080
echo 前端地址: http://localhost:5173
echo.
echo 如果后端启动失败，请查看后端窗口的错误信息
echo 可能需要配置数据库连接：backend/src/main/resources/application.properties
echo.
echo 按任意键关闭此窗口...
pause >nul
