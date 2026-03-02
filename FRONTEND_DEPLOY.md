# 前端部署说明

## 文件说明

- `index.html` - 应用入口页面
- `assets/` - 静态资源目录（CSS、JS、字体、图片等）

## 部署方式

### 方式1：Nginx部署（推荐生产环境）

#### 1. 安装Nginx

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install nginx
```

**CentOS/RHEL:**
```bash
sudo yum install nginx
```

**Windows:** 下载 nginx.exe 从官网

#### 2. 配置Nginx

创建或编辑配置文件 `/etc/nginx/sites-available/sharedlab`:

```nginx
server {
    listen 80;
    server_name your-domain.com;  # 修改为你的域名或IP
    
    # 前端静态文件目录
    root /var/www/sharedlab/frontend;
    index index.html;
    
    # 处理Vue Router的history模式
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    # 反向代理后端API
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # 超时设置
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }
    
    # 静态资源缓存
    location /assets {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
    
    # Gzip压缩
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript 
               application/x-javascript application/xml+rss 
               application/javascript application/json;
}
```

#### 3. 部署文件

```bash
# 创建目录
sudo mkdir -p /var/www/sharedlab/frontend

# 复制文件
sudo cp -r frontend/* /var/www/sharedlab/frontend/

# 设置权限
sudo chown -R www-data:www-data /var/www/sharedlab
sudo chmod -R 755 /var/www/sharedlab
```

#### 4. 启用配置并重启

```bash
# 创建软链接
sudo ln -s /etc/nginx/sites-available/sharedlab /etc/nginx/sites-enabled/

# 测试配置
sudo nginx -t

# 重启Nginx
sudo systemctl restart nginx
```

### 方式2：Apache部署

#### 配置文件示例

```apache
<VirtualHost *:80>
    ServerName your-domain.com
    DocumentRoot /var/www/sharedlab/frontend
    
    <Directory /var/www/sharedlab/frontend>
        Options -Indexes +FollowSymLinks
        AllowOverride All
        Require all granted
        
        # Vue Router history模式
        RewriteEngine On
        RewriteBase /
        RewriteRule ^index\.html$ - [L]
        RewriteCond %{REQUEST_FILENAME} !-f
        RewriteCond %{REQUEST_FILENAME} !-d
        RewriteRule . /index.html [L]
    </Directory>
    
    # 代理API请求
    ProxyPreserveHost On
    ProxyPass /api http://localhost:8080/api
    ProxyPassReverse /api http://localhost:8080/api
</VirtualHost>
```

### 方式3：简单HTTP服务器（开发/测试）

#### Python (推荐)
```bash
cd frontend
python -m http.server 3000
# 或者 Python 2
python -m SimpleHTTPServer 3000
```

#### Node.js serve
```bash
npm install -g serve
serve -s frontend -p 3000
```

#### PHP
```bash
cd frontend
php -S localhost:3000
```

### 方式4：Docker部署

#### Dockerfile示例

```dockerfile
FROM nginx:alpine

# 复制前端文件
COPY frontend /usr/share/nginx/html

# 复制nginx配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

#### 构建和运行

```bash
docker build -t sharedlab-frontend .
docker run -d -p 80:80 --name sharedlab-web sharedlab-frontend
```

## HTTPS配置（推荐）

### 使用Let's Encrypt免费证书

```bash
# 安装certbot
sudo apt install certbot python3-certbot-nginx

# 获取证书
sudo certbot --nginx -d your-domain.com

# 自动续期
sudo certbot renew --dry-run
```

### Nginx HTTPS配置

```nginx
server {
    listen 443 ssl http2;
    server_name your-domain.com;
    
    ssl_certificate /etc/letsencrypt/live/your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;
    
    # SSL配置优化
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;
    
    # ... 其他配置同上 ...
}

# HTTP重定向到HTTPS
server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$server_name$request_uri;
}
```

## 环境变量配置

如果需要在运行时配置API地址，可以使用环境变量替换：

创建 `env-config.js`:
```javascript
window.ENV = {
  API_BASE_URL: '${API_BASE_URL}'
};
```

在HTML中引入：
```html
<script src="/env-config.js"></script>
```

## 验证部署

1. 访问首页：http://your-domain.com
2. 检查浏览器控制台是否有错误
3. 测试登录功能
4. 检查API请求是否正常

## 常见问题

### 1. 页面刷新404
确保配置了URL重写规则，将所有请求指向 index.html

### 2. API跨域问题
确保后端配置了CORS或使用Nginx反向代理

### 3. 静态资源404
检查 `assets/` 目录路径和权限

### 4. 路由不工作
确认使用了正确的路由模式（history模式需要服务器支持）

## 性能优化

1. **启用Gzip压缩**（配置见上方）

2. **设置静态资源缓存**
```nginx
location ~* \.(jpg|jpeg|png|gif|ico|css|js|woff|woff2)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}
```

3. **启用HTTP/2**
```nginx
listen 443 ssl http2;
```

4. **CDN加速**
将静态资源上传到CDN，修改资源路径

## 监控建议

1. 使用Nginx日志监控访问情况
2. 配置Google Analytics或百度统计
3. 使用Sentry监控前端错误
4. 配置uptime监控服务可用性
