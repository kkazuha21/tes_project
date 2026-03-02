@echo off
chcp 65001 >nul
echo 访问地址: http://localhost:5173/index.html
echo 用户名: admin  密码: 123456
pause
start http://localhost:5173/index.html
