@echo off
chcp 65001 >nul
echo ====================================
echo 共享Lab - 开发模式启动
echo ====================================
echo.

echo 此脚本将使用 Maven 直接运行，无需预先编译
echo 如果您的系统未安装 Maven，请：
echo 1. 安装 Maven: https://maven.apache.org/download.cgi
echo 2. 或使用 IDE (如 IntelliJ IDEA) 直接运行项目
echo.
pause

echo [1/2] 启动后端服务...
cd backend
start "SharedLab Backend" cmd /k "java -version && echo. && echo 正在启动... && echo. && (if exist mvnw.cmd (mvnw.cmd spring-boot:run) else (mvn spring-boot:run))"
cd ..

timeout /t 5 /nobreak >nul

echo [2/2] 启动前端服务...
cd frontend
if not exist "node_modules" (
    echo 正在安装前端依赖...
    call npm install
)
start "SharedLab Frontend" cmd /k "npm run dev"
cd ..

echo.
echo ====================================
echo 服务启动中...
echo ====================================
echo 后端: http://localhost:8080
echo 前端: http://localhost:5173
echo.
echo 请查看打开的窗口了解启动状态
echo.
pause
