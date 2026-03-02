# SharedLab JMeter 压测文件

本目录提供 SharedLab 后端接口的 JMeter `.jmx` 压测计划，以及在 Windows 下的一键运行脚本。

## 文件说明

- `SharedLab_API_LoadTest.jmx`：接口压测计划（登录 + 浏览 + 角色接口）
- `data/users.csv`：测试账号数据（默认：admin/teacher1/student1）
- `run-jmeter.ps1`：非 GUI 模式执行 + 生成 HTML 报告

## 前置条件

1. 后端已启动：默认 `http://localhost:8080`
2. 已安装 JMeter（建议 5.6+），并确保 `jmeter` 在 PATH 中
3. 测试账号可用（如密码不同，请修改 `data/users.csv`）

## 运行

在本目录打开 PowerShell：

```powershell
./run-jmeter.ps1 -Threads 50 -RampUp 20 -Duration 300
```

如后端不在本机或端口不同：

```powershell
./run-jmeter.ps1 -Host 127.0.0.1 -Port 8080 -Threads 50 -RampUp 20 -Duration 300
```

运行输出：
- `results/run_*.jtl`：原始结果
- `results/report_*`：HTML 报告目录

## 覆盖接口

- `POST /api/auth/login`
- `GET /api/devices`
- `GET /api/devices/{id}`（自动取列表第一条）
- `GET /api/categories`
- `GET /api/bookings/my`
- `GET /api/tickets/my`
- 角色条件执行：
  - `ADMIN/TEACHER`：`GET /api/bookings/all`、`GET /api/tickets`
  - `ADMIN`：`GET /api/users/statistics`

## 参数（-J 覆盖）

- `protocol`：默认 `http`
- `host`：默认 `localhost`
- `port`：默认 `8080`
- `threads`：并发线程数
- `rampUp`：爬升时间（秒）
- `duration`：持续时间（秒）
- `timeoutMs`：请求超时（毫秒）
- `thinkTimeMs`：请求间隔（毫秒）

---

备注：你提到的《测试要求.md》目前不在工作区内，我无法直接读取。把它复制到 `c:\SharedLab\local\测试要求.md`（或任何工作区路径）后，我可以按里面的并发/指标/覆盖范围把该 `.jmx` 精确调整。