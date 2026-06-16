-- 修复中文乱码问题 - 清空旧数据并重新插入正确的中文数据

-- 1. 清空消费记录表（保留用户表）
TRUNCATE TABLE consumption_record;

-- 2. 重新插入测试数据（使用正确的 UTF-8 编码）
INSERT INTO consumption_record (user_id, category, amount, description) VALUES
(2, '食堂', 15.50, '午餐'),
(2, '超市', 32.00, '购买生活用品'),
(2, '食堂', 12.00, '早餐'),
(3, '食堂', 18.00, '晚餐'),
(3, '网吧', 10.00, '上网2小时'),
(2, '图书馆', 5.00, '打印资料'),
(2, '咖啡店', 25.00, '下午茶'),
(2, '奶茶店', 18.00, '珍珠奶茶'),
(3, '健身房', 200.00, '月卡充值'),
(2, '电影院', 60.00, '看电影'),
(3, '餐厅', 120.00, '朋友聚餐');

-- 3. 验证数据
SELECT id, user_id, category, amount, description, create_time 
FROM consumption_record 
ORDER BY id DESC 
LIMIT 20;
