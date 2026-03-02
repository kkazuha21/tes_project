# **SharedLab 系统接口文档（按参考模板整理）**

> 说明：本文件基于当前仓库后端代码（Spring Boot）实际存在的接口整理。
> 
> 默认后端本地地址：`http://localhost:8080/`（见 backend/src/main/resources/application.properties）
> 
> 统一鉴权方式：JWT（`Authorization: Bearer <token>`）

---

# **0. 鉴权与通用约定**

## **0.1 JWT 获取与携带**
- 通过“登录接口”获取 JWT。
- 调用需要登录的接口时，在请求头带：
  - `Authorization: Bearer <token>`

## **0.2 权限说明（基于后端配置）**
- 公开接口（无需登录）：
  - `/api/auth/**`（登录/注册）
  - `/api/devices/**`（设备列表/详情接口在过滤器层面放行；但管理员写操作仍受 `@PreAuthorize` 限制）
- 需要登录：其余 `/api/**`
- 需要管理员/教师角色：
  - `GET /api/bookings/all`
  - `GET /api/tickets`（获取全部报修单）
  - `PUT /api/tickets/{id}/status`
- 仅管理员：
  - `/api/users/**`（整个控制器标注 `@PreAuthorize("hasRole('ADMIN')")`）
  - 设备/分类写操作：`POST/PUT/DELETE /api/devices...`、`POST/PUT/DELETE /api/categories...`

## **0.3 Content-Type**
- JSON 请求体：`Content-Type: application/json`

---

# **1. 注册接口（SharedLab）**

## **1.1 前置条件**
无

## **1.2 基本信息**
**Path**：`/api/auth/register`  
Method：**POST**

## **1.3 接口描述**
### headers
| 参数名称 | 必填 | 取值 |
| --- | --- | --- |
| Content-Type | 是 | application/json |

### body（JSON）
后端当前直接接收 `User` 实体字段（最少需满足数据库约束）。

| 参数名称 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| username | string | 是 | 用户名（唯一） |
| email | string | 是 | 邮箱（唯一） |
| passwordHash | string | 是 | 注意：字段名是 `passwordHash`（后端会把它当作“明文密码”再加密存储） |
| role | string | 否 | 不传则默认 `STUDENT`（可选：`ADMIN`/`TEACHER`/`STUDENT`） |

示例：
```json
{
  "username": "student1",
  "email": "student1@example.com",
  "passwordHash": "123456",
  "role": "STUDENT"
}
```

## **1.4 返回数据**
### 注册成功（200）
```json
{
  "message": "注册成功",
  "userId": 1,
  "username": "student1"
}
```

### 常见失败
- 400：参数非法/用户名或邮箱已存在（返回：`{"error":"..."}`）

---

# **2. 登录接口（SharedLab）**

## **2.1 前置条件**
已注册账号

## **2.2 基本信息**
**Path**：`/api/auth/login`  
Method：**POST**

## **2.3 接口描述**
### headers
| 参数名称 | 必填 | 取值 |
| --- | --- | --- |
| Content-Type | 是 | application/json |

### body（JSON）
| 参数名称 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| username | string | 是 | 用户名 |
| password | string | 是 | 密码 |

示例：
```json
{
  "username": "student1",
  "password": "123456"
}
```

## **2.4 返回数据**
### 登录成功（200）
返回 `JwtResponse`：
```json
{
  "token": "<jwt-token>",
  "type": "Bearer",
  "userId": 1,
  "username": "student1",
  "email": "student1@example.com",
  "role": "STUDENT"
}
```

### 登录失败（401）
```json
{ "error": "用户名或密码错误" }
```

---

# **3. 设备接口**

## **3.1 获取设备列表（支持筛选）**
### 前置条件
无（公开）

### 基本信息
**Path**：`/api/devices`  
Method：**GET**

### query 参数
| 参数名称 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| status | string | 否 | 设备状态：`AVAILABLE` / `IN_USE` / `MAINTENANCE` |
| categoryId | long | 否 | 分类 ID |

### 返回（200）
返回 `Device` 数组（字段示例）：
```json
[
  {
    "id": 1,
    "name": "示波器",
    "status": "AVAILABLE",
    "location": "A楼-101",
    "desc": "100MHz",
    "deviceCategory": {
      "id": 2,
      "name": "电子仪器",
      "description": "...",
      "createdAt": "2026-01-01T10:00:00",
      "updatedAt": "2026-01-01T10:00:00"
    }
  }
]
```

## **3.2 获取设备详情**
### 前置条件
无（公开）

### 基本信息
**Path**：`/api/devices/{id}`  
Method：**GET**

### path 参数
| 参数名称 | 类型 | 必填 |
| --- | --- | --- |
| id | long | 是 |

### 返回（200）
同上：返回单个 `Device`。

## **3.3 创建设备（仅管理员）**
### 前置条件
需要管理员登录（`ADMIN`）

### 基本信息
**Path**：`/api/devices`  
Method：**POST**

### headers
| 参数名称 | 必填 | 取值 |
| --- | --- | --- |
| Content-Type | 是 | application/json |
| Authorization | 是 | Bearer <token> |

### body（JSON）
直接接收 `Device`（常用字段）：
| 参数名称 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| name | string | 是 | 设备名称 |
| status | string | 否 | 默认 `AVAILABLE` |
| location | string | 否 | 存放位置 |
| desc | string | 否 | 描述 |
| deviceCategory | object | 否 | 传 `{ "id": <分类id> }` 进行关联（取决于前端/实际调用方式） |

### 返回（200）
返回创建后的 `Device`。

## **3.4 更新设备（仅管理员）**
**Path**：`/api/devices/{id}`  
Method：**PUT**  
headers/body 同创建。

## **3.5 删除设备（仅管理员）**
**Path**：`/api/devices/{id}`  
Method：**DELETE**  
返回：200（空 body）。

---

# **4. 设备分类接口**

## **4.1 获取分类列表**
### 前置条件
需要登录（`/api/**` 默认鉴权；实际前端是否调用公开需以安全配置为准）

### 基本信息
**Path**：`/api/categories`  
Method：**GET**

### 返回（200）
返回 `CategoryResponse` 数组：
```json
[
  {
    "id": 1,
    "name": "电子仪器",
    "description": "...",
    "deviceCount": 10,
    "createdAt": "2026-01-01T10:00:00",
    "updatedAt": "2026-01-01T10:00:00"
  }
]
```

## **4.2 获取分类详情**
**Path**：`/api/categories/{id}`  
Method：**GET**

## **4.3 创建分类（仅管理员）**
**Path**：`/api/categories`  
Method：**POST**  
headers：`Authorization` 必填

body（JSON，DeviceCategory）：
```json
{ "name": "电子仪器", "description": "..." }
```

## **4.4 更新分类（仅管理员）**
**Path**：`/api/categories/{id}`  
Method：**PUT**

## **4.5 删除分类（仅管理员）**
**Path**：`/api/categories/{id}`  
Method：**DELETE**

---

# **5. 预约接口（Bookings）**

## **5.1 创建预约**
### 前置条件
需要登录

### 基本信息
**Path**：`/api/bookings`  
Method：**POST**

### headers
| 参数名称 | 必填 | 取值 |
| --- | --- | --- |
| Content-Type | 是 | application/json |
| Authorization | 是 | Bearer <token> |

### body（JSON：BookingRequest）
| 参数名称 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| deviceId | long | 是 | 设备 ID |
| startTime | string | 是 | ISO 日期时间，且必须为未来时间 |
| endTime | string | 是 | ISO 日期时间，且必须为未来时间 |

示例：
```json
{
  "deviceId": 1,
  "startTime": "2026-01-03T10:00:00",
  "endTime": "2026-01-03T12:00:00"
}
```

### 返回
- 成功（200）：`BookingResponse`
- 冲突（409）：`{"error":"..."}`（如时间冲突）
- 参数非法（400）：`{"error":"..."}` 或全局校验错误结构

## **5.2 获取我的预约列表**
**Path**：`/api/bookings/my`  
Method：**GET**  
headers：`Authorization` 必填

返回（200）：`BookingResponse[]`

## **5.3 查询某设备预约（用于日历）**
**Path**：`/api/bookings`  
Method：**GET**

query 参数：
| 参数名称 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| deviceId | long | 是 | 设备 ID |
| startTime | string | 否 | ISO 日期时间（范围查询起） |
| endTime | string | 否 | ISO 日期时间（范围查询止） |

返回（200）：`BookingResponse[]`

## **5.4 取消预约**
**Path**：`/api/bookings/{id}/cancel`  
Method：**PUT**  
headers：`Authorization` 必填

返回：成功（200）`BookingResponse`；失败（400）`{"error":"..."}`

## **5.5 获取全部预约（管理员/教师）**
**Path**：`/api/bookings/all`  
Method：**GET**  
headers：`Authorization` 必填

要求角色：`ADMIN` 或 `TEACHER`

---

# **6. 报修接口（Tickets）**

## **6.1 提交报修单**
### 前置条件
需要登录

### 基本信息
**Path**：`/api/tickets`  
Method：**POST**

### headers
| 参数名称 | 必填 | 取值 |
| --- | --- | --- |
| Content-Type | 是 | application/json |
| Authorization | 是 | Bearer <token> |

### body（JSON：TicketSubmit）
| 参数名称 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| deviceId | long | 是 | 设备 ID |
| title | string | 是 | 标题 |
| content | string | 否 | 内容 |
| severity | string | 否 | 严重程度（字符串，具体枚举未限制） |

返回（200）：`TicketResponse`

## **6.2 获取我的报修单**
**Path**：`/api/tickets/my`  
Method：**GET**  
headers：`Authorization` 必填

返回（200）：`TicketResponse[]`

## **6.3 获取全部报修单（管理员/教师）**
**Path**：`/api/tickets`  
Method：**GET**  
headers：`Authorization` 必填

要求角色：`ADMIN` 或 `TEACHER`

## **6.4 获取报修单详情**
**Path**：`/api/tickets/{id}`  
Method：**GET**  
headers：`Authorization` 必填

## **6.5 更新报修单状态（管理员/教师）**
**Path**：`/api/tickets/{id}/status`  
Method：**PUT**

query 参数：
| 参数名称 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| status | string | 是 | `SUBMITTED` / `PROCESSING` / `CLOSED` / `REJECTED` |

返回（200）：`TicketResponse`

---

# **7. 用户管理接口（仅管理员）**

> 说明：该控制器整体要求 `ADMIN`。

## **7.1 获取用户列表（可按角色筛选）**
**Path**：`/api/users`  
Method：**GET**

query 参数：
| 参数名称 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| role | string | 否 | `ADMIN` / `TEACHER` / `STUDENT` |

返回（200）：`UserResponse[]`

## **7.2 获取用户统计**
**Path**：`/api/users/statistics`  
Method：**GET**

返回（200）：
```json
{
  "totalUsers": 10,
  "adminCount": 1,
  "teacherCount": 2,
  "studentCount": 7
}
```

## **7.3 根据 ID 获取用户**
**Path**：`/api/users/{id}`  
Method：**GET**

返回（200）：`UserResponse`

## **7.4 创建用户**
**Path**：`/api/users`  
Method：**POST**

body（JSON：UserRequest）
| 参数名称 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| username | string | 是 | 3-50 字符 |
| email | string | 是 | 邮箱 |
| role | string | 是 | 角色 |
| status | int | 否 | 默认 1 |
| password | string | 是 | 创建时必填，>=6 |

返回：201（`UserResponse`）

## **7.5 更新用户**
**Path**：`/api/users/{id}`  
Method：**PUT**

body：`UserRequest`（`password` 可选，传则重置密码）

## **7.6 启用/禁用用户**
**Path**：`/api/users/{id}/toggle-status`  
Method：**PUT**

## **7.7 删除用户**
**Path**：`/api/users/{id}`  
Method：**DELETE**

返回：204（无 body）

---

# **附录：常见错误返回（与实现相关）**
- 参数校验失败（400）：全局异常处理会返回带 `errors` 的结构
- 资源不存在（404）：全局异常处理返回 `{timestamp,status,error,message}`
- 权限不足（403）：通常为 Spring Security/方法鉴权返回
- 未登录（401）：通常为 Spring Security 返回
