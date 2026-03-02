#!/bin/bash

echo "===================================="
echo "共享Lab - 实验室设备共享平台"
echo "===================================="
echo ""

echo "[1/4] 检查环境..."
echo ""

# 检查 Java
if ! command -v java &> /dev/null; then
    echo "[错误] 未检测到Java，请先安装Java 17+"
    exit 1
fi
echo "[✓] Java 已安装"

# 检查 Node.js
if ! command -v node &> /dev/null; then
    echo "[错误] 未检测到Node.js，请先安装Node.js 16+"
    exit 1
fi
echo "[✓] Node.js 已安装"

# 检查 MySQL
if ! command -v mysql &> /dev/null; then
    echo "[警告] 未检测到MySQL命令行工具，请确保MySQL正在运行"
fi
echo ""

echo "[2/4] 编译后端..."
cd backend
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "[错误] 后端编译失败"
    exit 1
fi
cd ..
echo "[✓] 后端编译完成"
echo ""

echo "[3/4] 安装前端依赖..."
cd frontend
if [ ! -d "node_modules" ]; then
    npm install
    if [ $? -ne 0 ]; then
        echo "[错误] 前端依赖安装失败"
        exit 1
    fi
fi
cd ..
echo "[✓] 前端依赖已安装"
echo ""

echo "[4/4] 启动服务..."
echo ""
echo "正在启动后端服务 (端口 8080)..."
cd backend
mvn spring-boot:run &
BACKEND_PID=$!
cd ..

sleep 5

echo "正在启动前端服务 (端口 5173)..."
cd frontend
npm run dev &
FRONTEND_PID=$!
cd ..

echo ""
echo "===================================="
echo "服务启动成功！"
echo "===================================="
echo "后端地址: http://localhost:8080"
echo "前端地址: http://localhost:5173"
echo ""
echo "测试账号:"
echo "用户名: admin / teacher1 / student1"
echo "密码: password"
echo ""
echo "按 Ctrl+C 停止所有服务"

# 等待中断信号
trap "kill $BACKEND_PID $FRONTEND_PID; exit" INT
wait
