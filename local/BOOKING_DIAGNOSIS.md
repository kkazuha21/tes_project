# 🔍 学生预约失败 - 诊断步骤

## 问题描述
学生无法创建设备预约

## 后端测试结果 ✅

### API 端点测试
- ✅ POST `/api/bookings` - 创建预约成功
- ✅ GET `/api/bookings?deviceId=13` - 获取设备预约列表成功
- ✅ GET `/api/bookings/my` - 获取我的预约成功
- ✅ 学生账号登录成功 (student1/123456)

### 测试数据
```json
{
  "deviceId": 13,
  "startTime": "2025-11-07T10:00:00",
  "endTime": "2025-11-07T12:00:00"
}
```

**结果**: 预约ID 21 创建成功，状态 CONFIRMED

## 前端组件检查 ✅

### BookingCalendar.vue
- ✅ 时间格式正确: `YYYY-MM-DDTHH:mm:ss`
- ✅ API 调用代码正确: `api.post('/bookings', {...})`
- ✅ 错误处理完善
- ✅ 表单验证存在

### DeviceDetail.vue
- ✅ 正确导入 BookingCalendar 组件
- ✅ loadBookings() 使用正确的 API: `/bookings?deviceId=xxx`
- ✅ handleBookingCreated() 回调函数存在

### API Service (api.js)
- ✅ Token 自动附加（拦截器）
- ✅ 401/403 错误处理
- ✅ baseURL: '/api'
- ✅ Vite proxy 配置正确

## 需要用户提供的信息 ❓

### 请您帮忙检查以下内容：

1. **浏览器控制台错误**
   - 打开 http://localhost:5173/index.html
   - 登录 student1/123456
   - 进入设备详情页
   - 按 F12 打开控制台
   - 尝试创建预约
   - **截图或复制控制台中的红色错误信息**

2. **网络请求详情**
   - 在 F12 开发者工具中
   - 切换到 "Network"（网络）标签
   - 尝试创建预约
   - 找到 `bookings` 请求
   - **检查：**
     - 请求状态码（200? 400? 401? 500?）
     - 请求头是否包含 Authorization
     - 请求体内容
     - 响应内容

3. **具体错误现象**
   - [ ] 点击"立即预约"按钮没有反应
   - [ ] 显示"预约中..."但一直转圈
   - [ ] 弹出错误提示：______________
   - [ ] 页面跳转或刷新
   - [ ] 其他：______________

## 可能的问题和解决方案

### 问题 1: Token 过期
**现象**: 显示 401 未授权  
**解决**: 重新登录

### 问题 2: 时间冲突
**现象**: 显示"该时间段已被预约"  
**解决**: 选择其他时间段

### 问题 3: 时间格式错误
**现象**: 400 Bad Request  
**检查**: 
```javascript
// 开发者工具 Console 中运行:
const picker = document.querySelector('.el-date-picker');
console.log('Start Time:', bookingForm.startTime);
console.log('End Time:', bookingForm.endTime);
```

### 问题 4: CORS 错误
**现象**: "Access-Control-Allow-Origin" 错误  
**解决**: Vite 已配置 proxy，不应该出现

### 问题 5: 组件未加载
**现象**: 预约表单不显示  
**检查**: DeviceDetail 页面是否完整显示

## 快速测试工具

### 工具 1: 调试页面
打开浏览器访问: `C:\SharedLab\booking-debug.html`

**测试步骤**:
1. 点击"登录" - 确认获得 token
2. 点击"获取设备" - 确认能访问 API
3. 点击"使用默认时间"
4. 点击"创建预约" - 查看结果

### 工具 2: PowerShell 测试
```powershell
# 在 PowerShell 中运行:
cd C:\SharedLab

# 测试登录
$body = @{username='student1';password='123456'} | ConvertTo-Json
$response = Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/login' -Method Post -Body $body -ContentType 'application/json'
$token = $response.token
Write-Host "Token: $token"

# 测试创建预约
$headers = @{Authorization="Bearer $token"}
$bookingBody = @{
    deviceId=13
    startTime='2025-11-08T10:00:00'
    endTime='2025-11-08T12:00:00'
} | ConvertTo-Json

Invoke-RestMethod -Uri 'http://localhost:8080/api/bookings' -Method Post -Body $bookingBody -ContentType 'application/json' -Headers $headers
```

## 临时解决方案

如果前端预约一直失败，可以暂时使用调试工具创建预约，然后在前端查看预约列表。

## 下一步

请提供：
1. 浏览器控制台的错误信息（截图或文字）
2. Network 标签中 bookings 请求的详情
3. 具体的错误现象描述

有了这些信息，我可以精确定位问题并修复。
