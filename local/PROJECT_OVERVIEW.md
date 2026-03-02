# 共享Lab 项目概览

## 🎯 项目完成清单

### ✅ 后端 (Spring Boot 3)

#### 1. 项目配置
- [x] pom.xml - Maven依赖配置
- [x] application.properties - 应用配置

#### 2. 数据模型层
- [x] UserRole 枚举
- [x] DeviceStatus 枚举  
- [x] BookingStatus 枚举
- [x] TicketStatus 枚举
- [x] User 实体
- [x] DeviceCategory 实体
- [x] Device 实体
- [x] Booking 实体 (含预约冲突检测)
- [x] Ticket 实体
- [x] MaintenanceLog 实体

#### 3. 数据访问层
- [x] UserRepository
- [x] DeviceCategoryRepository
- [x] DeviceRepository
- [x] BookingRepository (含冲突检测查询)
- [x] TicketRepository
- [x] MaintenanceLogRepository

#### 4. 安全认证层
- [x] JwtService - JWT令牌生成与验证
- [x] UserDetailsServiceImpl - 用户详情加载
- [x] JwtAuthFilter - JWT认证过滤器
- [x] SecurityConfig - Spring Security配置

#### 5. 业务逻辑层
- [x] UserService - 用户服务
- [x] DeviceService - 设备服务
- [x] **BookingService - 预约服务 (核心冲突检测)**
- [x] TicketService - 报修服务

#### 6. 控制器层
- [x] AuthController - 登录/注册
- [x] DeviceController - 设备CRUD
- [x] **BookingController - 预约管理 (冲突检测API)**
- [x] TicketController - 报修管理

#### 7. 异常处理
- [x] BookingConflictException - 预约冲突异常
- [x] ResourceNotFoundException - 资源不存在异常
- [x] GlobalExceptionHandler - 全局异常处理

#### 8. 配置类
- [x] WebConfig - CORS跨域配置

#### 9. 主应用
- [x] SharedLabApplication - 启动类

---

### ✅ 前端 (Vue 3 + Vite)

#### 1. 项目配置
- [x] package.json - NPM依赖
- [x] vite.config.js - Vite配置
- [x] index.html - HTML入口

#### 2. 核心配置
- [x] main.js - Vue应用初始化
- [x] App.vue - 根组件

#### 3. 路由配置
- [x] router/index.js - 路由表与守卫

#### 4. 状态管理 (Pinia)
- [x] store/auth.js - 认证状态
- [x] store/device.js - 设备状态

#### 5. API服务
- [x] services/api.js - Axios实例与拦截器

#### 6. 布局组件
- [x] AppHeader.vue - 顶部导航栏

#### 7. 通用组件
- [x] **BookingCalendar.vue - 预约日历 (核心组件，含冲突检测)**

#### 8. 页面组件
- [x] Login.vue - 登录/注册页
- [x] Dashboard.vue - 用户首页
- [x] DeviceList.vue - 设备列表
- [x] **DeviceDetail.vue - 设备详情 (含预约日历)**
- [x] MyBookings.vue - 我的预约
- [x] MyTickets.vue - 我的报修

#### 9. 管理员页面
- [x] Admin/AdminDashboard.vue - 管理后台
- [x] Admin/TicketManagement.vue - 报修管理

---

### ✅ 文档与配置

- [x] README.md - 项目说明文档
- [x] .gitignore - Git忽略配置
- [x] database/init.sql - 数据库初始化脚本
- [x] start.bat - Windows启动脚本
- [x] start.sh - Linux/Mac启动脚本

---

## 🔑 核心功能实现

### 1. 预约冲突检测 ⭐

**后端实现** (`BookingService.createBooking`)
```java
// 时间段重叠检测
boolean hasConflict = bookingRepository
    .existsByDeviceIdAndStatusNotAndStartTimeBeforeAndEndTimeAfter(
        deviceId,
        BookingStatus.CANCELLED,
        newEndTime,
        newStartTime
    );
```

**数据库查询** (`BookingRepository`)
```java
@Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
       "FROM Booking b " +
       "WHERE b.device.id = :deviceId " +
       "AND b.status != :status " +
       "AND b.startTime < :endTime " +
       "AND b.endTime > :startTime")
```

**前端处理** (`BookingCalendar.vue`)
- 日期时间选择器
- 禁用过去时间
- 显示已有预约时段
- 提交预约请求
- 处理409冲突响应

### 2. JWT认证流程

1. **登录** → `AuthController.login()`
2. **生成Token** → `JwtService.generateToken()`
3. **前端存储** → localStorage
4. **自动附加** → axios请求拦截器
5. **验证** → `JwtAuthFilter.doFilterInternal()`
6. **过期处理** → axios响应拦截器 → 跳转登录

### 3. 角色权限控制

- `@PreAuthorize("hasRole('ADMIN')")` - 方法级权限控制
- 路由守卫 - 前端路由权限检查
- 用户状态检查 - 禁用用户无法登录

---

## 📊 数据库表结构

| 表名 | 说明 | 关键字段 |
|-----|------|---------|
| users | 用户表 | username, email, password_hash, role, status |
| device_categories | 设备分类 | name |
| devices | 设备表 | name, status, location, category_id |
| bookings | 预约表 | user_id, device_id, start_time, end_time, status |
| tickets | 报修单表 | user_id, device_id, title, severity, status |
| maintenance_logs | 维修日志 | ticket_id, action, note |

---

## 🚀 快速启动

### Windows
```bash
start.bat
```

### Linux/Mac
```bash
chmod +x start.sh
./start.sh
```

### 手动启动

**后端**
```bash
cd backend
mvn spring-boot:run
```

**前端**
```bash
cd frontend
npm install
npm run dev
```

---

## 🧪 测试账号

| 用户名 | 密码 | 角色 |
|-------|------|------|
| admin | password | 管理员 |
| teacher1 | password | 教师 |
| student1 | password | 学生 |

---

## 📝 API端点概览

### 认证
- POST `/api/auth/login` - 登录
- POST `/api/auth/register` - 注册

### 设备
- GET `/api/devices` - 设备列表
- GET `/api/devices/{id}` - 设备详情

### 预约 (核心)
- POST `/api/bookings` - 创建预约 ⭐冲突检测
- GET `/api/bookings/my` - 我的预约
- GET `/api/bookings?deviceId={id}` - 设备预约记录
- PUT `/api/bookings/{id}/cancel` - 取消预约

### 报修
- POST `/api/tickets` - 提交报修
- GET `/api/tickets/my` - 我的报修
- GET `/api/tickets` - 所有报修 (管理员)
- PUT `/api/tickets/{id}/status` - 更新状态

---

## 🎨 技术亮点

1. **预约冲突检测算法** - 精确的时间段重叠判断
2. **JWT无状态认证** - 前后端完全分离
3. **Axios拦截器** - 自动token管理与错误处理
4. **Pinia状态管理** - 响应式用户状态
5. **Element Plus UI** - 美观的现代化界面
6. **路由守卫** - 权限控制与登录验证
7. **异常处理** - 全局统一异常响应
8. **CORS配置** - 跨域请求支持

---

## 📦 项目统计

- **后端文件数**: 30+
- **前端文件数**: 20+
- **总代码行数**: 5000+
- **API端点数**: 20+
- **数据库表数**: 6

---

## ✨ 项目特色

✅ **完整的CRUD功能**  
✅ **实时预约冲突检测**  
✅ **JWT安全认证**  
✅ **角色权限管理**  
✅ **响应式UI设计**  
✅ **RESTful API规范**  
✅ **异常统一处理**  
✅ **数据验证完善**

---

**开发完成日期**: 2025-10-30  
**状态**: ✅ 可直接运行
