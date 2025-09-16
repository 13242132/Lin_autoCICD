-- ========================================
-- 初始数据库架构
-- 创建所有必要的表并插入初始数据
-- ========================================

-- ========================================
-- 用户表
-- 存储系统用户信息
-- ========================================
-- 创建用户表
CREATE TABLE IF NOT EXISTS user_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 用户ID，自增主键
    username VARCHAR(50) NOT NULL, -- 用户名，不允许为空，最大长度50
    password VARCHAR(100) NOT NULL, -- 密码，不允许为空，最大长度100
    nickname VARCHAR(50), -- 用户昵称，允许为空，最大长度50
    avatar VARCHAR(255), -- 头像URL，允许为空，最大长度255
    role VARCHAR(20), -- 用户角色，允许为空，最大长度20
    status VARCHAR(20) NOT NULL, -- 用户状态，不允许为空，最大长度20
    created_at TIMESTAMP NOT NULL -- 创建时间，不允许为空
);

-- ========================================
-- 任务表
-- 存储任务信息
-- ========================================
-- 创建任务表
CREATE TABLE IF NOT EXISTS task_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 任务ID，自增主键
    title VARCHAR(255) NOT NULL, -- 任务标题，不允许为空，最大长度255
    description VARCHAR(1000), -- 任务描述，允许为空，最大长度1000
    priority VARCHAR(10) NOT NULL, -- 优先级（HIGH/MEDIUM/LOW），不允许为空，最大长度10
    due_date TIMESTAMP NOT NULL, -- 截止日期，不允许为空
    assignee VARCHAR(50) NOT NULL, -- 分配给的用户名，不允许为空，最大长度50
    status VARCHAR(20) NOT NULL, -- 任务状态（PENDING/IN_PROGRESS/COMPLETED），不允许为空，最大长度20
    created_at TIMESTAMP NOT NULL, -- 创建时间，不允许为空
    updated_at TIMESTAMP -- 更新时间，允许为空
);

-- ========================================
-- 通知表
-- 存储用户通知信息
-- ========================================
-- 创建通知表
CREATE TABLE IF NOT EXISTS notification_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 通知ID，自增主键
    user_id BIGINT NOT NULL, -- 接收通知的用户ID，不允许为空
    type VARCHAR(30) NOT NULL, -- 通知类型（SYSTEM/TASK/ALERT），不允许为空，最大长度30
    content VARCHAR(500) NOT NULL, -- 通知内容，不允许为空，最大长度500
    is_read BOOLEAN NOT NULL DEFAULT FALSE, -- 是否已读，默认false
    created_at TIMESTAMP NOT NULL, -- 创建时间，不允许为空
    sent_at TIMESTAMP, -- 发送时间，允许为空
    FOREIGN KEY (user_id) REFERENCES user_tbl(id) -- 外键约束，关联用户表的ID
);

-- ========================================
-- 审计日志表
-- 存储系统操作日志
-- ========================================
-- 创建审计日志表
CREATE TABLE IF NOT EXISTS audit_log_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 日志ID，自增主键
    user_id BIGINT NOT NULL, -- 操作用户的ID，不允许为空
    action VARCHAR(50) NOT NULL, -- 操作动作（LOGIN/CREATE_TASK/UPDATE_PROFILE），不允许为空，最大长度50
    target_type VARCHAR(20) NOT NULL, -- 目标类型（USER/TASK/NOTIFICATION），不允许为空，最大长度20
    target_id BIGINT NOT NULL, -- 目标对象ID，不允许为空
    details TEXT, -- 操作详情，允许为空
    ip_address VARCHAR(45), -- IP地址，允许为空，最大长度45
    created_at TIMESTAMP NOT NULL -- 创建时间，不允许为空
);

-- ========================================
-- 插入初始数据
-- ========================================

-- 插入用户数据
INSERT INTO user_tbl (username, password, nickname, avatar, role, status, created_at)
VALUES 
    ('admin', 'admin123', 'Admin User', 'https://example.com/avatar_admin.jpg', 'ADMIN', 'ACTIVE', '2025-01-01 10:00:00'),
    ('alice', 'user123', 'Alice Smith', 'https://example.com/avatar_alice.jpg', 'USER', 'ACTIVE', '2025-01-02 11:00:00'),
    ('bob', 'user456', 'Bob Johnson', 'https://example.com/avatar_bob.jpg', 'USER', 'INACTIVE', '2025-01-03 12:00:00');

-- 插入任务数据
INSERT INTO task_tbl (title, description, priority, due_date, assignee, status, created_at, updated_at)
VALUES 
    ('Complete project setup', 'Setup the project environment and dependencies', 'HIGH', '2025-01-15 18:00:00', 'alice', 'IN_PROGRESS', '2025-01-05 09:00:00', '2025-01-06 14:00:00'),
    ('Write documentation', 'Document the API endpoints and usage examples', 'MEDIUM', '2025-01-20 18:00:00', 'bob', 'PENDING', '2025-01-07 10:00:00', NULL),
    ('Fix bug #123', 'Fix the issue with user login', 'HIGH', '2025-01-10 12:00:00', 'alice', 'COMPLETED', '2025-01-08 11:00:00', '2025-01-09 15:00:00');

-- 插入通知数据
INSERT INTO notification_tbl (user_id, type, content, is_read, created_at, sent_at)
SELECT u.id, 'SYSTEM', 'Your account has been successfully created', FALSE, '2025-01-04 13:00:00', '2025-01-04 13:05:00'
FROM user_tbl u
WHERE u.username = 'bob';

INSERT INTO notification_tbl (user_id, type, content, is_read, created_at, sent_at)
SELECT u.id, 'TASK', 'A new task has been assigned to you: Complete project setup', TRUE, '2025-01-05 09:30:00', '2025-01-05 09:35:00'
FROM user_tbl u
WHERE u.username = 'alice';

INSERT INTO notification_tbl (user_id, type, content, is_read, created_at, sent_at)
SELECT u.id, 'ALERT', 'Your task Fix bug #123 is overdue', FALSE, '2025-01-11 10:00:00', NULL
FROM user_tbl u
WHERE u.username = 'alice';

-- 插入审计日志数据
INSERT INTO audit_log_tbl (user_id, action, targetType, target_id, details, ip_address, created_at)
SELECT u.id, 'USER_REGISTER', 'USER', u.id, 'User registered with username: ' || u.username, '192.168.1.1', '2025-01-01 10:05:00'
FROM user_tbl u;

INSERT INTO audit_log_tbl (user_id, action, targetType, target_id, details, ip_address, created_at)
SELECT u.id, 'CREATE_TASK', 'TASK', t.id, 'Created task titled: ' || t.title, '192.168.1.1', '2025-01-05 09:10:00'
FROM user_tbl u
JOIN task_tbl t ON t.assignee = u.username
WHERE u.username = 'alice' AND t.title = 'Complete project setup';

INSERT INTO audit_log_tbl (user_id, action, targetType, target_id, details, ip_address, created_at)
SELECT u.id, 'TASK_UPDATE', 'TASK', t.id, 'Updated task status to COMPLETED', '192.168.1.1', '2025-01-09 15:10:00'
FROM user_tbl u
JOIN task_tbl t ON t.assignee = u.username
WHERE u.username = 'alice' AND t.title = 'Fix bug #123';