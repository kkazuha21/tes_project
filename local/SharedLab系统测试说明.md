文档编号：**SHAREDLAB-CS002 V1.0**

**共享实验器材平台（SharedLab）**

**软件测试说明**

编写：__________  
审核：__________  
批准：__________  

日期：2026年1月6日

---

## 文档变更记录

| 版本号 | 变更日期 | 主要变更内容 | 变更理由 | 变更人 |
| --- | --- | --- | --- | --- |
| V1.0 | 2026-01-06 | 创建测试说明初稿（结合现有文档与接口） | 作为测试执行依据 |  |

---

## 1. 简介

### 1.1 系统概述

SharedLab 为实验室设备共享与预约平台，具备登录/注册、设备查看、预约调度（含冲突检测）、报修处理等能力。

### 1.2 编写目的

描述测试准备、测试项分解、测试用例设计与执行方法，并提供测试用例与测试需求的追踪依据。

### 1.3 参考文档

| 文档 | 位置 |
| --- | --- |
| 接口文档 | SHAREDLAB_API.md |
| 项目概览 | local/PROJECT_OVERVIEW.md |
| 用例描述 | local/项目用例描述.md |
| 功能测试用例表 | local/功能测试用例表.md |
| 功能测试指南 | local/TEST_GUIDE.md |
| JMeter 压测说明 | tests/jmeter/README.md |

### 1.4 术语说明

| 术语 | 解释 |
| --- | --- |
| UC | Use Case，用例编号（见 local/项目用例描述.md 与 local/SHAREDLAB_测试用例描述_补全.md） |
| 断言 | 对响应码/响应体字段/关键字进行校验 |
| 事务（Transaction） | JMeter 中对一组请求统计整体耗时 |

---

## 2. 测试准备

### 2.1 被测软件准备

- 前端：Vue 3 + Vite（默认 http://localhost:5173）
- 后端：Spring Boot（默认 http://localhost:8080）
- 鉴权：JWT（`Authorization: Bearer <token>`）

### 2.2 测试环境准备

| 类别 | 配置 |
| --- | --- |
| 操作系统 | Windows 11 |
| 浏览器 | Chrome/Edge |
| 后端运行环境 | JDK 17+（项目启动脚本以仓库为准） |
| 数据库 | MySQL（开发环境） |
| 网络 | 本地回环或局域网 |

### 2.3 测试数据准备

- 账号：admin/teacher1/student1（见 local/TEST_GUIDE.md）
- 业务数据：至少存在若干设备与分类；建议存在预约与报修用于列表/查询场景

---

## 3. 正式测试说明

### 3.1 测试项分解

按用例/模块分解测试项：

1) 用户权限（UC001）
- 注册：唯一性、必填、格式、密码长度
- 登录：正确登录、错误密码、未登录访问拦截、跨角色访问拦截

2) 设备管理（UC002）
- 设备列表：加载、筛选（状态/分类）、空态
- 设备详情：详情加载、错误 id 的异常处理
- 管理员维护：新增/编辑/删除（若接口/页面具备）

3) 预约调度（UC003）
- 查看设备预约时段（用于日历）
- 创建预约：正常创建
- 冲突检测：重叠时间段返回 409
- 取消预约：状态变更

4) 故障报修（UC005）
- 提交报修
- 查看我的报修
- 教师/管理员查看全部报修
- 教师/管理员更新状态

### 3.2 测试用例设计方法

- 等价类划分：账号密码、邮箱格式、设备状态枚举等
- 边界值分析：密码长度、时间（过去/未来/相等）、时间段重叠边界
- 错误推测法：token 缺失、角色越权、空列表/空字段、接口返回结构变化

### 3.3 测试用例来源与追踪

- 功能用例：local/功能测试用例表.md（包含 LOGIN/REG/BOOK/TICKET 等用例）
- 用例描述与需求分解：local/项目用例描述.md、local/SHAREDLAB_测试用例描述_补全.md
- API 覆盖：SHAREDLAB_API.md

建议在执行时补充“测试用例—测试需求—测试项”追踪表（可按 UC/用例编号映射）。

---

## 4. 接口/性能测试说明（JMeter）

### 4.1 测试目标

- 验证关键接口在并发下可用性与稳定性
- 采集响应时间、错误率、吞吐量

### 4.2 测试计划文件

- JMX：tests/jmeter/SharedLab_API_LoadTest.jmx
- 数据集：tests/jmeter/data/users.csv
- 运行脚本：tests/jmeter/run-jmeter.ps1

### 4.3 覆盖接口（摘要）

- `POST /api/auth/login`
- `GET /api/devices`、`GET /api/devices/{id}`
- `GET /api/categories`
- `GET /api/bookings/my`、`GET /api/tickets/my`
- 角色接口：
  - `ADMIN/TEACHER`：`GET /api/bookings/all`、`GET /api/tickets`
  - `ADMIN`：`GET /api/users/statistics`

### 4.4 参数化与执行方式

支持通过 `-J` 参数覆盖：`threads`、`rampUp`、`duration`、`timeoutMs`、`thinkTimeMs`、`host/port`。

示例：

```powershell
cd tests/jmeter
./run-jmeter.ps1 -Threads 50 -RampUp 20 -Duration 300
```

> 性能阈值（如 P95 响应时间、错误率阈值）需以《测试要求.md》为准；该文件目前不在工作区内，后续纳入后可补齐“达标判定”。

### 4.5 人员分工与用例记录

- 4 人接口测试分工：local/接口测试分工.md
- 用例数量与提交材料汇总：local/评分表.md

建议每位成员在其负责模块下输出“接口用例清单与执行记录”（含：用例编号、前置条件、步骤、期望结果、实际结果、证据截图/日志路径），并在评分表中统计数量。

---

## 5. 回归测试说明

当出现缺陷修复或功能变更时：
- 优先回归受影响模块对应的用例（例如预约冲突检测相关逻辑）
- 再执行关键路径冒烟：登录 → 设备列表 → 设备详情 → 我的预约/我的报修

---

## 6. 输出物

- 功能测试执行记录（建议在 local/功能测试用例表.md 中补齐“实测结果/结论/测试人员/时间”）
- 接口/性能测试结果：JTL + HTML 报告（由 run-jmeter.ps1 生成）
- 软件测试报告（见 local/SharedLab-软件测试报告.md）
- 接口测试分工与统计：local/接口测试分工.md、local/评分表.md
