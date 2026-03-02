# 共享Lab - 实验室设备共享平台

一个完整的前后端分离的实验室设备共享与预约平台。

## 技术栈

### 后端
- Spring Boot 3.1.5
- Spring Security + JWT
- Spring Data JPA
- MySQL 8.0
- Maven

### 前端
- Vue 3
- Vite
- Vue Router
- Pinia (状态管理)
- Element Plus (UI组件库)
- Axios

## 项目结构

```
SharedLab/
├── backend/                    # Spring Boot 后端
│   ├── src/
│   │   └── main/
│   │       ├── java/com/sharedlab/
│   │       │   ├── config/            # 配置类 (Security, CORS)
│   │       │   ├── controller/        # REST API 控制器
│   │       │   ├── model/
│   │       │   │   ├── entity/        # JPA 实体
│   │       │   │   ├── dto/           # 数据传输对象
│   │       │   │   └── enums/         # 枚举类型
│   │       │   ├── repository/        # JPA Repository
│   │       │   ├── service/           # 业务逻辑层
│   │       │   ├── security/          # 安全相关 (JWT过滤器)
│   │       │   └── exception/         # 异常处理
│   │       └── resources/
│   │           └── application.properties
│   └── pom.xml
│
└── frontend/                   # Vue 3 前端
    ├── src/
    │   ├── assets/
    │   ├── components/
    │   │   ├── layout/            # 布局组件 (Header)
    │   │   └── common/            # 通用组件 (BookingCalendar)
    │   ├── router/                # Vue Router 配置
    │   ├── store/                 # Pinia 状态管理
    │   ├── services/              # API 服务 (axios)
    │   ├── views/                 # 页面组件
    │   │   ├── Login.vue
    │   │   ├── Dashboard.vue
    │   │   ├── DeviceList.vue
    │   │   ├── DeviceDetail.vue
    │   │   ├── MyBookings.vue
    │   │   ├── MyTickets.vue
    │   │   └── Admin/             # 管理员页面
    │   ├── App.vue
    │   └── main.js
    ├── package.json
    └── vite.config.js
```

## 核心功能

### 1. 用户认证
- JWT token 认证
- 用户登录/注册
- 角色权限控制 (学生/教师/管理员)

### 2. 设备管理
- 设备列表浏览
- 设备详情查看
- 设备状态筛选 (可用/使用中/维护中)
- 管理员设备管理

### 3. 预约系统 ⭐核心功能
- **时间冲突检测**: 防止同一设备在相同时间段被重复预约
- 日历可视化选择预约时间
- 查看已有预约时段
- 取消预约
- 我的预约列表

### 4. 报修系统
- 提交设备报修单
- 报修单状态跟踪
- 管理员审核处理报修单
- 严重程度分级

## 安装和运行

### 前置要求
- Java 17+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### 1. 数据库配置

创建 MySQL 数据库:

```sql
CREATE DATABASE shared_lab CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

修改后端配置文件 `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/shared_lab?useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=你的用户名
spring.datasource.password=你的密码
```

### 2. 启动后端

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端将在 http://localhost:8080 启动

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端将在 http://localhost:5173 启动

### 4. 初始化测试数据

首次运行时，需要注册一个管理员账号。可以通过以下步骤:

1. 在登录页面注册一个账号
2. 直接在数据库中将该用户的 role 修改为 'ADMIN':

```sql
UPDATE users SET role = 'ADMIN' WHERE username = '你的用户名';
```

## API 端点

### 认证相关
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册

### 设备相关
- `GET /api/devices` - 获取设备列表 (支持status和categoryId筛选)
- `GET /api/devices/{id}` - 获取设备详情
- `POST /api/devices` - 创建设备 (管理员)
- `PUT /api/devices/{id}` - 更新设备 (管理员)
- `DELETE /api/devices/{id}` - 删除设备 (管理员)

### 预约相关
- `POST /api/bookings` - 创建预约 (含冲突检测)
- `GET /api/bookings/my` - 获取当前用户预约
- `GET /api/bookings?deviceId={id}` - 获取设备预约记录
- `PUT /api/bookings/{id}/cancel` - 取消预约
- `GET /api/bookings/all` - 获取所有预约 (管理员)

### 报修相关
- `POST /api/tickets` - 提交报修单
- `GET /api/tickets/my` - 获取当前用户报修单
- `GET /api/tickets` - 获取所有报修单 (管理员)
- `GET /api/tickets/{id}` - 获取报修单详情
- `PUT /api/tickets/{id}/status` - 更新报修单状态 (管理员)

## 核心业务逻辑说明

### 预约冲突检测算法

位于 `BookingService.createBooking()` 方法中:

```java
// 时间段重叠检测条件:
// 新预约的开始时间 < 已有预约的结束时间 
// AND 
// 新预约的结束时间 > 已有预约的开始时间
// AND 
// 已有预约状态不是 CANCELLED

boolean hasConflict = bookingRepository
    .existsByDeviceIdAndStatusNotAndStartTimeBeforeAndEndTimeAfter(
        deviceId,
        BookingStatus.CANCELLED,
        newEndTime,
        newStartTime
    );

if (hasConflict) {
    throw new BookingConflictException("该时间段设备已被预约");
}
```

### JWT 认证流程

1. 用户登录 → 验证用户名密码
2. 生成JWT token (有效期24小时)
3. 前端保存token到localStorage
4. 每次API请求自动在Header中附加 `Authorization: Bearer {token}`
5. 后端JwtAuthFilter拦截请求验证token
6. Token过期或无效 → 自动跳转登录页

## 默认测试账号

可以自行注册账号，或使用以下SQL插入测试数据:

```sql
-- 密码: password (BCrypt加密后的值)
INSERT INTO users (username, email, password_hash, role, status, created_at) 
VALUES 
('admin', 'admin@lab.com', '$2a$10$XYZ...', 'ADMIN', 1, NOW()),
('teacher', 'teacher@lab.com', '$2a$10$ABC...', 'TEACHER', 1, NOW()),
('student', 'student@lab.com', '$2a$10$DEF...', 'STUDENT', 1, NOW());
```

## 常见问题

### 1. 跨域问题
后端已配置CORS，允许 localhost:5173 访问。如果前端端口不同，需要修改 `WebConfig.java`。

### 2. JWT 密钥配置
请在生产环境中修改 `application.properties` 中的 `jwt.secret` 为强密钥。

### 3. 数据库连接失败
检查MySQL服务是否启动，用户名密码是否正确。

## 开发建议

- 后端使用Lombok简化代码，IDE需安装Lombok插件
- 前端建议使用Vue DevTools进行调试
- API测试可使用Postman或Apifox

## 许可证

MIT License

---

**作者**: AI Assistant  
**日期**: 2025-10-30
