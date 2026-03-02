@echo off
chcp 65001 > nul
echo ========================================
echo   运行同学乙单元测试
echo ========================================
echo.

REM 切换到后端项目目录
cd /d C:\SharedLab\backend

echo [1/3] 编译测试类...
echo.

REM 编译测试文件到target目录
javac -cp "target/classes;%USERPROFILE%\.m2\repository\org\junit\jupiter\junit-jupiter-api\5.10.1\junit-jupiter-api-5.10.1.jar;%USERPROFILE%\.m2\repository\org\mockito\mockito-core\5.7.0\mockito-core-5.7.0.jar;%USERPROFILE%\.m2\repository\org\springframework\boot\spring-boot-starter-test\3.2.0\spring-boot-starter-test-3.2.0.jar" ^
    -d target/test-classes ^
    -encoding UTF-8 ^
    C:\SharedLab\SharedLab_Test_Submission\同学乙_脚本与截图\单元测试\DeviceServiceTest.java ^
    C:\SharedLab\SharedLab_Test_Submission\同学乙_脚本与截图\单元测试\DeviceTest.java

if errorlevel 1 (
    echo.
    echo ❌ 编译失败！正在使用Maven重新构建...
    echo.
    echo [使用Maven构建和测试]
    call mvnw.cmd clean test -Dtest=DeviceServiceTest,DeviceTest
    goto :end
)

echo ✅ 编译成功
echo.
echo [2/3] 运行DeviceServiceTest...
echo.

REM 使用Maven运行测试
call mvnw.cmd test -Dtest=DeviceServiceTest

echo.
echo [3/3] 运行DeviceTest...
echo.

call mvnw.cmd test -Dtest=DeviceTest

:end
echo.
echo ========================================
echo   测试完成
echo ========================================
echo.
pause
