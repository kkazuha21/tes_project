# PowerShell脚本：使用Maven运行单元测试
# 编码：UTF-8

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  运行同学乙单元测试 (Maven)" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 获取当前脚本所在目录（测试文件目录）
$sourceDir = $PSScriptRoot
$backendDir = "C:\SharedLab\backend"
$targetDir = "$backendDir\src\test\java\com\sharedlab"

Write-Host "[1/4] 创建测试目录..." -ForegroundColor Yellow

# 创建目录结构
if (-not (Test-Path "$targetDir\service")) {
    New-Item -ItemType Directory -Path "$targetDir\service" -Force | Out-Null
}
if (-not (Test-Path "$targetDir\model\entity")) {
    New-Item -ItemType Directory -Path "$targetDir\model\entity" -Force | Out-Null
}

Write-Host "✅ 目录创建完成" -ForegroundColor Green
Write-Host ""

Write-Host "[2/4] 复制测试文件..." -ForegroundColor Yellow

# 复制测试文件
Copy-Item "$sourceDir\DeviceServiceTest.java" "$targetDir\service\" -Force
Copy-Item "$sourceDir\DeviceTest.java" "$targetDir\model\entity\" -Force

Write-Host "✅ 文件复制完成" -ForegroundColor Green
Write-Host ""

Write-Host "[3/4] 运行DeviceServiceTest..." -ForegroundColor Yellow
Write-Host ""

# 切换到后端目录
Set-Location $backendDir

# 运行DeviceServiceTest
& .\mvnw.cmd test -Dtest=DeviceServiceTest

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "✅ DeviceServiceTest 通过" -ForegroundColor Green
} else {
    Write-Host ""
    Write-Host "❌ DeviceServiceTest 失败" -ForegroundColor Red
}

Write-Host ""
Write-Host "[4/4] 运行DeviceTest..." -ForegroundColor Yellow
Write-Host ""

# 运行DeviceTest
& .\mvnw.cmd test -Dtest=DeviceTest

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "✅ DeviceTest 通过" -ForegroundColor Green
} else {
    Write-Host ""
    Write-Host "❌ DeviceTest 失败" -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  测试完成" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 显示测试报告位置
Write-Host "📊 测试报告位置：" -ForegroundColor Cyan
Write-Host "   HTML报告: backend\target\surefire-reports\index.html" -ForegroundColor Gray
Write-Host "   文本报告: backend\target\surefire-reports\*.txt" -ForegroundColor Gray
Write-Host ""

Read-Host "按回车键退出"
