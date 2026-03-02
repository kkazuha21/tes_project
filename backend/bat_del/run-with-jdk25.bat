@echo off
REM 临时用 JDK25 运行 mvnw（不修改系统级别环境变量）
setlocal
set "JAVA_HOME=C:\Program Files\Java\jdk-25"
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo Using JAVA_HOME=%JAVA_HOME%
mvnw.cmd clean spring-boot:run -DskipTests
endlocal
pause
