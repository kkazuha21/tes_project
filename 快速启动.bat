@echo off
chcp 65001 >nul
echo =============================================
echo   共享实验室平台 - 一键启动
echo =============================================
echo.

echo [1/3] 检查服务状态...
netstat -ano | findstr ":8080" >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ 后端已在运行 (端口 8080)
) else (
    echo ► 启动后端...
    start "SharedLab Backend" powershell -NoExit -Command "cd C:\SharedLab\backend; $env:JAVA_HOME='C:\Users\xzy20\jdk-21'; $env:PATH='C:\Users\xzy20\jdk-21\bin;'+$env:PATH; & 'C:\Users\xzy20\.m2\wrapper\dists\apache-maven-3.9.5-bin\32db9c34\apache-maven-3.9.5\bin\mvn.cmd' spring-boot:run"
    timeout /t 2 >nul
)

netstat -ano | findstr ":5173" >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ 前端已在运行 (端口 5173)
) else (
    echo ► 启动前端...
    start "SharedLab Frontend" powershell -NoExit -Command "Set-Location C:\SharedLab\frontend; npm run dev"
)

echo.
echo [2/3] 等待服务启动...
echo     后端需要约 40 秒
echo     前端需要约 15 秒
echo.

echo [3/3] 完成!
echo.
echo =============================================
echo   访问地址: http://localhost:5173
echo   API地址:  http://localhost:8080
echo =============================================
echo.
echo 提示: 
echo  - 不要关闭启动的窗口
echo  - 按任意键打开浏览器...
echo.
pause >nul
start http://localhost:5173
