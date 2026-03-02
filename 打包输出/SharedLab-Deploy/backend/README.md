# 后端部署说明

## 文件说明

- `shared-lab-platform-1.0.0.jar` - Spring Boot可执行jar包（包含所有依赖）
- `application.properties` - 配置文件

## 环境要求

- Java 21 或更高版本
- MySQL 8.0 或更高版本

## 配置说明

### application.properties 配置项

```properties
# 数据库配置（必须修改）
spring.datasource.url=jdbc:mysql://localhost:3306/sharedlab?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8
spring.datasource.username=root
spring.datasource.password=your_password

# 服务器端口（可选）
server.port=8080

# JWT配置（生产环境必须修改）
jwt.secret=your-secret-key-min-256-bits-long-for-hs256-algorithm
jwt.expiration=86400000

# 日志配置（可选）
logging.level.com.sharedlab=INFO
logging.file.name=logs/app.log
```

## 部署步骤

### 1. 数据库初始化

```sql
CREATE DATABASE sharedlab CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

然后执行项目中的SQL初始化脚本。

### 2. 修改配置文件

根据实际环境修改 `application.properties`：
- 数据库地址、用户名、密码
- JWT密钥（生产环境必须使用强密钥）
- 其他可选配置

### 3. 启动应用

#### Windows:
```cmd
java -jar shared-lab-platform-1.0.0.jar
```

#### Linux:
```bash
java -jar shared-lab-platform-1.0.0.jar
```

#### 后台运行（Linux）:
```bash
nohup java -jar shared-lab-platform-1.0.0.jar > output.log 2>&1 &
```

#### 使用systemd服务（Linux推荐）:

创建服务文件 `/etc/systemd/system/sharedlab.service`:
```ini
[Unit]
Description=SharedLab Platform
After=network.target mysql.service

[Service]
Type=simple
User=appuser
WorkingDirectory=/opt/sharedlab
ExecStart=/usr/bin/java -jar /opt/sharedlab/shared-lab-platform-1.0.0.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启动服务：
```bash
sudo systemctl daemon-reload
sudo systemctl enable sharedlab
sudo systemctl start sharedlab
sudo systemctl status sharedlab
```

### 4. 验证部署

访问 http://localhost:8080/api/auth/test 应该返回JSON响应。

## 常见问题

### 1. 端口被占用
修改 `application.properties` 中的 `server.port`

### 2. 数据库连接失败
- 检查MySQL是否运行
- 检查数据库地址、用户名、密码是否正确
- 检查防火墙设置

### 3. JWT验证失败
确保 `jwt.secret` 至少256位（32个字符）

### 4. 日志查看
- 默认日志输出到控制台
- 如配置了日志文件，查看 `logs/app.log`

## 性能优化建议

1. 使用JVM参数优化：
```bash
java -Xms512m -Xmx2g -XX:+UseG1GC -jar shared-lab-platform-1.0.0.jar
```

2. 启用生产配置：
```bash
java -jar shared-lab-platform-1.0.0.jar --spring.profiles.active=prod
```

3. 配置数据库连接池（在application.properties）：
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

## 安全建议

1. 修改默认JWT密钥
2. 使用HTTPS
3. 修改默认管理员密码
4. 配置防火墙规则
5. 定期备份数据库
6. 启用SQL审计日志
