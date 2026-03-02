# SharedLab 项目部署包

## 目录结构

```
SharedLab-Deploy/
├── backend/                          # 后端部署文件
│   ├── shared-lab-platform-1.0.0.jar # Spring Boot fat-jar
│   ├── application.properties        # 配置文件（需根据实际环境修改）
│   └── README.md                     # 后端部署说明
└── frontend/                         # 前端部署文件
    ├── assets/                       # 静态资源
    ├── index.html                    # 入口页面
    └── README.md                     # 前端部署说明
```

## 快速部署

### 1. 数据库准备

1. 创建MySQL数据库（建议MySQL 8.0+）
2. 执行数据库初始化脚本
3. 配置数据库连接信息

### 2. 后端部署

1. 进入backend目录
2. 修改application.properties配置文件：
   - 数据库连接信息
   - 服务器端口（默认8080）
   - JWT密钥
3. 运行命令启动：
   ```bash
   java -jar shared-lab-platform-1.0.0.jar
   ```

### 3. 前端部署

#### 方式1：Nginx部署（推荐）
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    root /path/to/frontend;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

#### 方式2：简单HTTP服务器
```bash
cd frontend
python -m http.server 3000
```

### 4. 访问系统

- 前端地址：http://your-domain.com 或 http://localhost:3000
- 后端API：http://your-domain.com/api 或 http://localhost:8080

### 默认账户

- 管理员：admin / admin123
- 教师：teacher / teacher123
- 学生：student / student123

## 注意事项

1. 确保Java 21+环境
2. 确保MySQL数据库正常运行
3. 前端需要配置API地址（在构建时已配置）
4. 生产环境请修改JWT密钥和数据库密码
5. 建议使用HTTPS协议部署

## 技术栈

- 后端：Spring Boot 3.2.0 + Spring Security + JWT + MySQL
- 前端：Vue 3 + Vite + Element Plus + Pinia
