@echo off
chcp 65001 >nul
echo 正在修复 Maven 项目结构...
echo.

cd /d "%~dp0backend\src\main\java"

REM 检查是否存在错误的路径结构
if exist "main\java\com\sharedlab" (
    echo 发现错误的包结构，正在修复...
    
    REM 如果正确的路径不存在，创建它
    if not exist "com" mkdir com
    if not exist "com\sharedlab" mkdir com\sharedlab
    
    REM 移动所有文件到正确位置
    echo 移动文件...
    xcopy /E /I /Y "main\java\com\sharedlab\*" "com\sharedlab\"
    
    REM 删除错误的目录
    echo 删除旧目录...
    rd /S /Q "main"
    
    echo.
    echo [成功] 包结构已修复！
) else (
    echo [提示] 包结构正确，无需修复。
)

echo.
echo 当前包结构：
tree /F com\sharedlab /A

echo.
pause
