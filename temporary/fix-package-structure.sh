#!/bin/bash

echo "正在修复 Maven 项目结构..."
echo ""

cd "$(dirname "$0")/backend/src/main/java"

# 检查是否存在错误的路径结构
if [ -d "main/java/com/sharedlab" ]; then
    echo "发现错误的包结构，正在修复..."
    
    # 如果正确的路径不存在，创建它
    mkdir -p com/sharedlab
    
    # 移动所有文件到正确位置
    echo "移动文件..."
    cp -r main/java/com/sharedlab/* com/sharedlab/
    
    # 删除错误的目录
    echo "删除旧目录..."
    rm -rf main
    
    echo ""
    echo "[成功] 包结构已修复！"
else
    echo "[提示] 包结构正确，无需修复。"
fi

echo ""
echo "当前包结构："
find com/sharedlab -type f -name "*.java" | head -20

echo ""
