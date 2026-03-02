-- 共享实验室平台 - 测试数据插入脚本
-- 在 MySQL 客户端中执行此脚本

USE shared_lab;

-- 1. 插入设备类别
INSERT INTO device_categories (name) VALUES
('显微镜'),
('天平'),
('烧杯'),
('电子示波器');

-- 2. 插入设备 (使用刚插入的类别ID)
INSERT INTO devices (name, category_id, description, location, status, created_at) VALUES
('奥林巴斯显微镜 CX23', 1, '光学显微镜,放大倍数40x-1000x', '实验室A-201', 'AVAILABLE', NOW()),
('徕卡显微镜 DM500', 1, '生物显微镜,适合教学使用', '实验室A-202', 'AVAILABLE', NOW()),
('梅特勒电子天平 ME204', 2, '精度0.1mg,最大称量220g', '实验室B-101', 'AVAILABLE', NOW()),
('赛多利斯天平 BSA224S', 2, '精度0.1mg,内部校准功能', '实验室B-102', 'IN_USE', NOW()),
('硼硅玻璃烧杯 500ml', 3, '耐高温烧杯,可用于加热', '实验室C-301', 'AVAILABLE', NOW()),
('泰克示波器 TDS2024C', 4, '4通道数字示波器,200MHz带宽', '实验室D-401', 'AVAILABLE', NOW());

-- 3. 查看插入的数据
SELECT '=== 设备类别 ===' AS '';
SELECT * FROM device_categories;

SELECT '=== 设备列表 ===' AS '';
SELECT d.id, d.name, c.name AS category, d.location, d.status 
FROM devices d 
JOIN device_categories c ON d.category_id = c.id;

-- 4. 可选: 插入一些预约记录(使用你创建的用户ID)
-- 注意: 需要先查看 users 表中的用户 ID,然后替换下面的 1, 2
-- INSERT INTO bookings (user_id, device_id, start_time, end_time, purpose, status, created_at) VALUES
-- (1, 1, NOW() + INTERVAL 1 DAY, NOW() + INTERVAL 1 DAY + INTERVAL 2 HOUR, '生物实验观察', 'CONFIRMED', NOW()),
-- (2, 3, NOW() + INTERVAL 2 DAY, NOW() + INTERVAL 2 DAY + INTERVAL 1 HOUR, '化学药品称量', 'PENDING', NOW());

SELECT '=== 插入完成 ===' AS '';
