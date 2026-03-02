文档编号：**SHAREDLAB-CS003 V1.0**

**共享实验器材平台（SharedLab）**

**软件测试报告**

编写：__________  
审核：__________  
批准：__________  

日期：2026年1月6日

---

## 文档变更记录

| 版本号 | 变更日期 | 主要变更内容 | 变更理由 | 变更人 |
| --- | --- | --- | --- | --- |
| V1.0 | 2026-01-06 | 创建测试报告初稿（汇总现有测试文档） | 项目交付/课程作业需要 |  |

---

## 1. 简介

### 1.0 人员分工与统计

- 接口测试分工：local/接口测试分工.md
- 用例数量统计与提交材料清单：local/评分表.md

### 1.1 系统概述

SharedLab 为实验室设备共享与预约平台，提供登录/注册、设备管理、预约调度（含冲突检测）、故障报修等功能。

### 1.2 测试依据

本报告汇总自工作区内已有测试文档与执行记录（详见“参考文档”），并补充接口/性能测试资产（JMeter）。

### 1.3 参考文档

| 文档 | 位置 |
| --- | --- |
| 测试计划 | local/SharedLab-软件测试计划.md |
| 测试说明 | local/SharedLab-软件测试说明.md |
| 功能测试指南 | local/TEST_GUIDE.md |
| 功能测试完成报告 | local/TEST_COMPLETION_REPORT.md |
| 测试结果报告 | local/TEST_RESULT.md |
| 前端功能测试报告 | local/FRONTEND_TEST_REPORT.md |
| 接口文档 | SHAREDLAB_API.md |
| JMeter 压测说明 | tests/jmeter/README.md |

---

## 2. 测试资源

### 2.1 测试环境

| 类别 | 配置 |
| --- | --- |
| 前端 | http://localhost:5173 |
| 后端 | http://localhost:8080 |
| 数据库 | MySQL（开发环境） |
| 操作系统 | Windows 11 |
| 浏览器 | Chrome/Edge |

### 2.2 测试账号

| 角色 | 用户名 | 密码 | 备注 |
| --- | --- | --- | --- |
| 管理员 | admin | 123456 | JWT 返回角色 ADMIN |
| 教师 | teacher1 | 123456 | JWT 返回角色 TEACHER |
| 学生 | student1 | 123456 | JWT 返回角色 STUDENT |

---

## 3. 测试进度

> 说明：本仓库内已存在两次日期相近的测试记录（2025-11-06/2025-11-07）。如需以本次（2026-01-06）为“正式测试周期”，建议在执行完用例与 JMeter 后补填本表。

| 工作内容 | 时间 | 输出 |
| --- | --- | --- |
| 前端功能测试与路由/布局验证 | 2025-11-06 | local/FRONTEND_TEST_REPORT.md、local/TEST_COMPLETION_REPORT.md |
| UI/管理端页面修复与验证 | 2025-11-07 | local/TEST_RESULT.md、local/TEST_GUIDE.md |
| 接口/性能测试资产准备（JMeter） | 2026-01-06 | tests/jmeter/* |

---

## 4. 测试结果

### 4.1 接口可用性（基于现有记录）

根据 local/TEST_COMPLETION_REPORT.md、local/FRONTEND_TEST_REPORT.md 的描述：
- `POST /api/auth/login`：管理员/教师/学生登录均成功，返回 JWT Token 与角色信息
- `GET /api/devices`：设备列表接口可用（记录中提到返回 6 台设备）
- `GET /api/bookings/my`、`GET /api/tickets/my`：接口可用，认证正常
- `GET /api/tickets`、`GET /api/bookings/all`：记录中标注可用（权限受角色控制）

### 4.2 前端功能与页面可用性（基于现有记录）

根据 local/TEST_RESULT.md 与 local/TEST_GUIDE.md：
- 导航栏重复问题已解决（管理端/教师端移除顶部导航）
- 管理端页面“空白页面”问题已解决（路由指向实际组件）
- 管理端/教师端统计卡片展示已实现（预约/用户/分类/日志等页面）

### 4.3 已知限制/待后端支持项（基于现有记录）

根据 local/TEST_RESULT.md：
- 用户管理与系统日志部分功能可能仍依赖后端支持（如 `/api/logs` 等）；如接口未提供则属于阻塞/不适用

---

## 5. 缺陷与问题跟踪（模板）

> 当前工作区文档未提供统一的缺陷清单编号体系；如你已有缺陷表/issue，可把链接或文件放入工作区后我可同步整合。

| 缺陷编号 | 模块 | 问题描述 | 严重程度 | 状态 | 备注 |
| --- | --- | --- | --- | --- | --- |
|  |  |  |  |  |  |

---

## 6. 接口/性能测试结果（JMeter）

### 6.1 已交付的 JMeter 测试文件

- JMX：tests/jmeter/SharedLab_API_LoadTest.jmx
- 运行脚本：tests/jmeter/run-jmeter.ps1
- 账号数据：tests/jmeter/data/users.csv

### 6.2 本次执行结果

- 目前工作区仅完成“测试文件交付”，未在本报告中内置具体压测数值结果。
- 当你实际运行 `run-jmeter.ps1` 后，会生成：
  - `tests/jmeter/results/run_*.jtl`
  - `tests/jmeter/results/report_*`（HTML 报告）

建议将以下指标从 HTML 报告中摘录并回填：

| 指标 | 目标值 | 实测值 | 结论 |
| --- | --- | --- | --- |
| 并发线程数（threads） |  |  |  |
| 持续时间（duration） |  |  |  |
| 错误率 |  |  |  |
| 平均响应时间 |  |  |  |
| P95 响应时间 |  |  |  |
| 吞吐量（Throughput） |  |  |  |

---

## 7. 测试结论

- 功能层面：根据现有测试记录，登录认证、设备浏览、部分管理端页面修复与展示逻辑已验证通过。
- 接口/性能层面：已提供可直接运行的 JMeter 测试计划与脚本；待按《测试要求.md》执行后，可形成完整的性能达标结论。

---

## 8. 建议

1. 将《测试要求.md》纳入工作区（如 local/测试要求.md），明确性能指标阈值并据此调整/裁剪 JMeter 用例。
2. 执行一次完整的用例表（local/功能测试用例表.md），补齐“实测结果/结论/测试人员/测试时间”。
3. 如计划交付“正式报告”，建议补充统一缺陷清单（编号、严重程度、状态）。
