@echo off
chcp 65001 >nul
echo ====================================
echo 共享Lab - 启动（已修复）
echo ====================================
echo.

echo [1/2] 启动后端服务...
cd backend

REM 检查是否有系统 Maven
where mvn >nul 2>&1
if %errorlevel% equ 0 (
    echo 使用系统 Maven 启动...
    start "SharedLab Backend" cmd /k "mvn spring-boot:run"
) else (
    echo 使用 Maven Wrapper 启动...
    start "SharedLab Backend" cmd /k "mvnw.cmd spring-boot:run"
)

cd ..
echo [✓] 后端服务已启动
echo.

timeout /t 5 /nobreak >nul

echo [2/2] 启动前端服务...
cd frontend

REM 检查 node_modules 是否存在
if not exist "node_modules" (
    echo [警告] 前端依赖未安装，请先运行: fix-environment.bat
    cd ..
    pause
    exit /b 1
)

start "SharedLab Frontend" cmd /k "npm run dev"
cd ..
echo [✓] 前端服务已启动
echo.

echo ====================================
echo 服务启动成功！
echo ====================================
echo.
echo 后端地址: http://localhost:8080
echo 前端地址: http://localhost:5173
echo.
echo 测试账号:
echo 用户名: admin / teacher1 / student1
echo 密码: password
echo.
echo 注意：
echo 1. 首次启动后端需要下载依赖，请等待几分钟
echo 2. 确保 MySQL 数据库已启动
echo 3. 确保已导入 database/init.sql
echo.
echo 如果启动失败，请查看对应窗口的错误信息
echo.
pause
