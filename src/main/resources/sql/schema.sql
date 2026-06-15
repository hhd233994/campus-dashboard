-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    role VARCHAR(20) DEFAULT 'USER' COMMENT '用户角色：USER-普通用户, ADMIN-管理员',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);

-- 创建消费记录表
CREATE TABLE IF NOT EXISTS consumption_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    category VARCHAR(50) NOT NULL COMMENT '消费类别',
    amount DECIMAL(10, 2) NOT NULL COMMENT '消费金额',
    description VARCHAR(255) COMMENT '备注说明',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 插入测试用户数据（密码都是123456的BCrypt加密值）
-- BCrypt每次生成的密文都不同，但都可以验证密码123456
INSERT IGNORE INTO user (username, password, real_name, email, phone, role) VALUES
('admin', '$2a$10$49NVnzOpIe108MXGOWqom.fZ3S2FG0c2xY9cfEQ7yOjud1bwSmKqO', '管理员', 'admin@example.com', '13800138000', 'ADMIN'),
('zhangsan', '$2a$10$49NVnzOpIe108MXGOWqom.fZ3S2FG0c2xY9cfEQ7yOjud1bwSmKqO', '张三', 'zhangsan@example.com', '13800138001', 'USER'),
('lisi', '$2a$10$49NVnzOpIe108MXGOWqom.fZ3S2FG0c2xY9cfEQ7yOjud1bwSmKqO', '李四', 'lisi@example.com', '13800138002', 'USER');

-- 插入测试消费记录数据
INSERT IGNORE INTO consumption_record (user_id, category, amount, description) VALUES
(2, '食堂', 15.50, '午餐'),
(2, '超市', 32.00, '购买生活用品'),
(2, '食堂', 12.00, '早餐'),
(3, '食堂', 18.00, '晚餐'),
(3, '网吧', 10.00, '上网2小时'),
(2, '图书馆', 5.00, '打印资料');

-- 创建索引优化查询性能
CREATE INDEX IF NOT EXISTS idx_user_id ON consumption_record(user_id);
CREATE INDEX IF NOT EXISTS idx_create_time ON consumption_record(create_time);
CREATE INDEX IF NOT EXISTS idx_category ON consumption_record(category);
CREATE INDEX IF NOT EXISTS idx_username ON user(username);

-- 创建操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    operation VARCHAR(20) COMMENT '操作类型：ADD/UPDATE/DELETE/QUERY',
    module VARCHAR(50) COMMENT '模块名称',
    description VARCHAR(255) COMMENT '操作描述',
    ip VARCHAR(50) COMMENT '请求IP',
    method VARCHAR(100) COMMENT '请求方法',
    status_code INT COMMENT '响应状态码',
    duration BIGINT COMMENT '耗时（毫秒）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

CREATE INDEX IF NOT EXISTS idx_user_id_log ON operation_log(user_id);
CREATE INDEX IF NOT EXISTS idx_create_time_log ON operation_log(create_time);