# 预约管理模块性能测试 - GUI 启动脚本
# 使用 JMeter GUI 模式运行

Write-Host "=" * 60 -ForegroundColor Cyan
Write-Host "预约管理模块性能测试" -ForegroundColor Green
Write-Host "=" * 60 -ForegroundColor Cyan
Write-Host ""

# 检查当前目录
$currentDir = Get-Location
Write-Host "当前目录: $currentDir" -ForegroundColor Yellow
Write-Host ""

# 检查必要文件
$jmxFile = "SharedLab_Booking_LoadTest.jmx"
$csvFile = "users.csv"

if (-not (Test-Path $jmxFile)) {
    Write-Host "✗ 错误: 找不到测试文件 $jmxFile" -ForegroundColor Red
    exit 1
}

if (-not (Test-Path $csvFile)) {
    Write-Host "✗ 错误: 找不到用户数据文件 $csvFile" -ForegroundColor Red
    exit 1
}

Write-Host "✓ 测试文件检查通过" -ForegroundColor Green
Write-Host "  - JMX文件: $jmxFile" -ForegroundColor White
Write-Host "  - 用户数据: $csvFile" -ForegroundColor White
Write-Host ""

Write-Host "测试配置:" -ForegroundColor Yellow
Write-Host "  - 模块: 预约管理 (Booking)" -ForegroundColor White
Write-Host "  - 线程数: 20 (可在 Thread Group 中调整)" -ForegroundColor White
Write-Host "  - 循环次数: 10" -ForegroundColor White
Write-Host "  - Ramp-Up: 10秒" -ForegroundColor White
Write-Host "  - 总请求: 约 1000 个 (20线程 × 10循环 × 5请求)" -ForegroundColor White
Write-Host ""

Write-Host "测试场景:" -ForegroundColor Yellow
Write-Host "  1. 用户登录 (提取 token)" -ForegroundColor White
Write-Host "  2. 获取可用设备列表" -ForegroundColor White
Write-Host "  3. 创建预约 (随机时间段)" -ForegroundColor White
Write-Host "  4. 获取我的预约" -ForegroundColor White
Write-Host "  5. 查询设备预约情况" -ForegroundColor White
Write-Host ""

Write-Host "执行步骤:" -ForegroundColor Yellow
Write-Host "  1. 确保后端运行在 http://localhost:8080" -ForegroundColor White
Write-Host "  2. 点击绿色播放按钮启动测试" -ForegroundColor White
Write-Host "  3. 在聚合报告中查看性能指标" -ForegroundColor White
Write-Host "  4. 在图形结果中查看响应时间趋势" -ForegroundColor White
Write-Host ""

# 尝试查找 JMeter
$jmeterPaths = @(
    "C:\Program Files\apache-jmeter-5.1.1\bin\jmeter.bat",
    "C:\apache-jmeter-5.1.1\bin\jmeter.bat",
    "D:\apache-jmeter-5.1.1\bin\jmeter.bat",
    "C:\Program Files\apache-jmeter\bin\jmeter.bat",
    "$env:JMETER_HOME\bin\jmeter.bat"
)

$jmeterBat = $null
foreach ($path in $jmeterPaths) {
    if (Test-Path $path) {
        $jmeterBat = $path
        break
    }
}

if ($jmeterBat) {
    Write-Host "✓ 找到 JMeter: $jmeterBat" -ForegroundColor Green
    Write-Host ""
    Write-Host "正在启动 JMeter GUI..." -ForegroundColor Cyan
    & $jmeterBat -t $jmxFile
} else {
    Write-Host "✗ 未找到 JMeter" -ForegroundColor Red
    Write-Host ""
    Write-Host "请手动操作:" -ForegroundColor Yellow
    Write-Host "  1. 启动 JMeter GUI" -ForegroundColor White
    Write-Host "  2. 打开文件: $jmxFile" -ForegroundColor White
    Write-Host "  3. 运行测试" -ForegroundColor White
    Write-Host ""
    Write-Host "或设置 JMETER_HOME 环境变量后重试" -ForegroundColor Yellow
}
