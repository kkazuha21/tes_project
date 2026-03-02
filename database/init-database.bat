@echo off
chcp 65001 >nul 2>&1
echo ============================================
echo 共享实验室平台 - 数据库初始化
echo ============================================
echo.
echo 正在初始化数据库...
echo.

mysql -u root -p12345678 < "%~dp0init.sql"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ============================================
    echo 数据库初始化成功！
    echo ============================================
    echo.
    echo 已创建以下内容：
    echo - 数据库: shared_lab
    echo - 4个数据表: users, device_categories, devices, bookings, tickets
    echo - 测试用户账号（密码都是: password）:
    echo   * admin@lab.com (管理员)
    echo   * teacher1@lab.com (教师)
    echo   * student1@lab.com (学生)
    echo   * student2@lab.com (学生)
    echo - 6台测试设备
    echo - 3条预约记录
    echo - 2个报修单
    echo.
) else (
    echo.
    echo ============================================
    echo 数据库初始化失败！
    echo ============================================
    echo.
    echo 请检查：
    echo 1. MySQL 服务是否正在运行
    echo 2. root 密码是否正确（当前脚本中的密码: 12345678）
    echo 3. 如需修改密码，请编辑此脚本文件
    echo.
)

pause
