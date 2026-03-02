# 前端功能测试脚本
Write-Host "========== 共享实验室平台前端测试 ==========" -ForegroundColor Cyan

# 测试 1: 登录功能
Write-Host "`n[测试 1] 测试登录 API" -ForegroundColor Yellow
Write-Host "1.1 管理员登录..." -ForegroundColor Gray
try {
    $adminBody = @{username='admin';password='123456'} | ConvertTo-Json
    $adminResponse = Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/login' -Method Post -Body $adminBody -ContentType 'application/json'
    Write-Host "✓ 管理员登录成功 - 用户: $($adminResponse.username), 角色: $($adminResponse.role)" -ForegroundColor Green
    $adminToken = $adminResponse.token
} catch {
    Write-Host "✗ 管理员登录失败: $_" -ForegroundColor Red
}

Write-Host "1.2 教师登录..." -ForegroundColor Gray
try {
    $teacherBody = @{username='teacher1';password='123456'} | ConvertTo-Json
    $teacherResponse = Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/login' -Method Post -Body $teacherBody -ContentType 'application/json'
    Write-Host "✓ 教师登录成功 - 用户: $($teacherResponse.username), 角色: $($teacherResponse.role)" -ForegroundColor Green
    $teacherToken = $teacherResponse.token
} catch {
    Write-Host "✗ 教师登录失败: $_" -ForegroundColor Red
}

Write-Host "1.3 学生登录..." -ForegroundColor Gray
try {
    $studentBody = @{username='student1';password='123456'} | ConvertTo-Json
    $studentResponse = Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/login' -Method Post -Body $studentBody -ContentType 'application/json'
    Write-Host "✓ 学生登录成功 - 用户: $($studentResponse.username), 角色: $($studentResponse.role)" -ForegroundColor Green
    $studentToken = $studentResponse.token
} catch {
    Write-Host "✗ 学生登录失败: $_" -ForegroundColor Red
}

# 测试 2: 设备列表
Write-Host "`n[测试 2] 测试设备列表 API" -ForegroundColor Yellow
try {
    $headers = @{Authorization="Bearer $adminToken"}
    $devices = Invoke-RestMethod -Uri 'http://localhost:8080/api/devices' -Method Get -Headers $headers
    Write-Host "✓ 获取设备列表成功 - 共 $($devices.Count) 台设备" -ForegroundColor Green
    if ($devices.Count -gt 0) {
        $device = $devices[0]
        Write-Host "  示例设备: $($device.name) - 状态: $($device.status)" -ForegroundColor Gray
    }
} catch {
    Write-Host "✗ 获取设备列表失败: $_" -ForegroundColor Red
}

# 测试 3: 我的预约
Write-Host "`n[测试 3] 测试我的预约 API" -ForegroundColor Yellow
try {
    $headers = @{Authorization="Bearer $studentToken"}
    $bookings = Invoke-RestMethod -Uri 'http://localhost:8080/api/bookings/my' -Method Get -Headers $headers
    Write-Host "✓ 获取学生预约成功 - 共 $($bookings.Count) 条预约" -ForegroundColor Green
} catch {
    Write-Host "✗ 获取预约失败: $_" -ForegroundColor Red
}

# 测试 4: 我的报修
Write-Host "`n[测试 4] 测试我的报修 API" -ForegroundColor Yellow
try {
    $headers = @{Authorization="Bearer $studentToken"}
    $tickets = Invoke-RestMethod -Uri 'http://localhost:8080/api/tickets/my' -Method Get -Headers $headers
    Write-Host "✓ 获取学生报修成功 - 共 $($tickets.Count) 条报修" -ForegroundColor Green
} catch {
    Write-Host "✗ 获取报修失败: $_" -ForegroundColor Red
}

# 测试 5: 前端页面访问
Write-Host "`n[测试 5] 测试前端页面访问" -ForegroundColor Yellow
Write-Host "5.1 测试首页..." -ForegroundColor Gray
try {
    $response = Invoke-WebRequest -Uri 'http://localhost:5173/index.html' -UseBasicParsing
    Write-Host "✓ 首页访问成功 - 状态码: $($response.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "✗ 首页访问失败: $_" -ForegroundColor Red
}

Write-Host "`n========== 测试完成 ==========" -ForegroundColor Cyan
Write-Host "`n建议手动测试以下功能:" -ForegroundColor Yellow
Write-Host "1. 在浏览器中打开 http://localhost:5173/index.html" -ForegroundColor Gray
Write-Host "2. 使用以下账号登录测试:" -ForegroundColor Gray
Write-Host "   - 管理员: admin / 123456" -ForegroundColor Gray
Write-Host "   - 教师: teacher1 / 123456" -ForegroundColor Gray
Write-Host "   - 学生: student1 / 123456" -ForegroundColor Gray
Write-Host "3. 测试各个角色的界面和菜单导航" -ForegroundColor Gray
Write-Host "4. 测试设备列表、预约、报修等功能" -ForegroundColor Gray
