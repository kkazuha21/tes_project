@echo off
chcp 65001 >nul
echo 启动后端服务（跳过测试和编译）...
echo.

cd backend
mvn spring-boot:run -Dmaven.test.skip=true -Dmaven.compiler.source=21 -Dmaven.compiler.target=21
cd ..

pause
