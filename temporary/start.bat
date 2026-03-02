@echo off
chcp 65001 >nul
echo ====================================
echo 共享Lab - 实验室设备共享平台
echo ====================================
echo.

echo [1/4] 检查环境...
echo.

REM 检查 Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到Java，请先安装Java 17+
    pause
    exit /b 1
)
echo [✓] Java 已安装

REM 检查 Node.js
node -v >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到Node.js，请先安装Node.js 16+
    echo.
    echo 下载地址: https://nodejs.org/
    echo 建议安装: Node.js 18 LTS 或更高版本
    echo.
    echo 安装完成后，请重新运行此脚本
    echo.
    pause
    exit /b 1
)
echo [✓] Node.js 已安装

REM 检查 MySQL
mysql --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [警告] 未检测到MySQL命令行工具，请确保MySQL正在运行
)
echo.

echo [2/4] 编译后端...
cd backend

REM 检查是否有 Maven
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo [提示] 未检测到 Maven，将跳过编译步骤
    echo [提示] 首次运行将直接启动（需要Maven自动下载依赖）
    cd ..
    goto skip_build
)

call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo [警告] 后端编译失败，将尝试直接运行
)
cd ..
echo [✓] 后端准备完成
:skip_build
echo.

echo [3/4] 安装前端依赖...
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

echo [4/4] 启动服务...
echo.
echo 正在启动后端服务 (端口 8080)...
start "SharedLab Backend" cmd /k "cd backend && mvn spring-boot:run"

timeout /t 5 /nobreak >nul

echo 正在启动前端服务 (端口 5173)...
start "SharedLab Frontend" cmd /k "cd frontend && npm run dev"

echo.
echo ====================================
echo 服务启动成功！
echo ====================================
echo 后端地址: http://localhost:8080
echo 前端地址: http://localhost:5173
echo.
echo 测试账号:
echo 用户名: admin / teacher1 / student1
echo 密码: password
echo.
echo 按任意键关闭此窗口...
pause >nul
