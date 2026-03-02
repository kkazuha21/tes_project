@echo off
chcp 65001 >nul
echo ========================================
echo   前端诊断工具
echo ========================================
echo.

echo [1] 检查文件结构...
if exist "C:\SharedLab\frontend\index.html" (
    echo ✅ index.html 存在
) else (
    echo ❌ index.html 不存在
    pause
    exit /b 1
)

if exist "C:\SharedLab\frontend\src\main.js" (
    echo ✅ src\main.js 存在
) else (
    echo ❌ src\main.js 不存在
    pause
    exit /b 1
)

if exist "C:\SharedLab\frontend\src\App.vue" (
    echo ✅ src\App.vue 存在
) else (
    echo ❌ src\App.vue 不存在
    pause
    exit /b 1
)

echo.
echo [2] 检查 Node 进程...
tasklist | findstr "node.exe" >nul
if %errorlevel% equ 0 (
    echo ⚠️  Node 进程正在运行，正在停止...
    taskkill /F /IM node.exe >nul 2>&1
    timeout /t 2 >nul
)

echo.
echo [3] 启动前端服务...
cd /d C:\SharedLab\frontend
start "前端服务" cmd /k "npm run dev"

echo.
echo [4] 等待服务启动...
timeout /t 8 >nul

echo.
echo [5] 测试访问...
powershell -Command "try { $r = Invoke-WebRequest 'http://localhost:5173' -UseBasicParsing -TimeoutSec 5; Write-Host '✅ 前端运行正常'; Write-Host '状态码:' $r.StatusCode } catch { Write-Host '❌ 无法访问前端'; Write-Host '错误:' $_.Exception.Message }"

echo.
echo ========================================
echo 请在浏览器中打开: http://localhost:5173
echo ========================================
pause
