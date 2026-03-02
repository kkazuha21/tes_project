@echo off
REM 直接用 wrapper 已下载的 Maven 分发，用 JDK25 运行（临时环境）
setlocal
set "JAVA_HOME=C:\Program Files\Java\jdk-25"
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo Using JAVA_HOME=%JAVA_HOME%
"C:\Users\xzy20\.m2\wrapper\dists\apache-maven-3.9.5-bin\32db9c34\apache-maven-3.9.5\bin\mvn.cmd" -f "%~dp0pom.xml" clean spring-boot:run -DskipTests
endlocal
pause
