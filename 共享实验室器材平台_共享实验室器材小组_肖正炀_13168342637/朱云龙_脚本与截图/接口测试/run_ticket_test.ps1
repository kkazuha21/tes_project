# 运行 Ticket 接口功能测试
# 使用 JMeter GUI 模式

Write-Host "正在启动 JMeter GUI 模式运行 Ticket 接口测试..." -ForegroundColor Green
Write-Host "测试文件: Ticket_Test.jmx" -ForegroundColor Cyan
Write-Host ""
Write-Host "操作步骤:" -ForegroundColor Yellow
Write-Host "1. 确保后端服务运行在 http://localhost:8080" -ForegroundColor White
Write-Host "2. 点击绿色播放按钮 (或按 Ctrl+R) 运行测试" -ForegroundColor White
Write-Host "3. 在'察看结果树'中查看每个请求的执行结果" -ForegroundColor White
Write-Host "4. 在'聚合报告'中查看统计数据" -ForegroundColor White
Write-Host ""

# 尝试查找 JMeter
$jmeterPaths = @(
    "C:\Program Files\apache-jmeter-5.1.1\bin\jmeter.bat",
    "C:\apache-jmeter-5.1.1\bin\jmeter.bat",
    "D:\apache-jmeter-5.1.1\bin\jmeter.bat",
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
    Write-Host "找到 JMeter: $jmeterBat" -ForegroundColor Green
    & $jmeterBat -t "Ticket_Test.jmx"
} else {
    Write-Host "未找到 JMeter，请手动启动 JMeter 并打开 Ticket_Test.jmx" -ForegroundColor Red
    Write-Host "或者设置 JMETER_HOME 环境变量" -ForegroundColor Yellow
}
