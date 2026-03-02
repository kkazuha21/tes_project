@echo off
chcp 65001 >nul
cls
echo ========================================
echo   角色权限系统 - 测试指南
echo ========================================
echo.
echo 【系统说明】
echo 现在有三种用户角色，每种角色有不同的界面和功能
echo.
echo 【测试账号】
echo.
echo ????? 学生账号:
echo    用户名: student1
echo    密码: 123456
echo    界面: 紫色主题
echo    功能: 查看设备、预约、报修
echo.
echo ????? 教师账号:
echo    用户名: teacher1
echo    密码: 123456
echo    界面: 粉红主题
echo    功能: 设备管理、预约管理、报修处理
echo.
echo ????? 管理员账号:
echo    用户名: admin
echo    密码: 123456
echo    界面: 蓝色主题
echo    功能: 完整系统管理权限
echo.
echo ========================================
echo 按任意键打开登录页面...
pause >nul
start http://localhost:5173/index.html
