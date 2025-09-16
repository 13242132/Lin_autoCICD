-- ========================================
-- 初始数据库架构
-- 创建所有必要的表并插入初始数据
-- ========================================

-- ========================================
-- 创建用户表
-- ========================================
-- user_tbl: 存储用户信息
-- id: 用户唯一标识，自增主键
-- username: 用户名，不允许为空，最大长度20
-- email: 邮箱地址，不允许为空，最大长度100
-- password: 登录密码，不允许为空，最大长度100
-- status: 用户状态，默认为active，最大长度20
-- avatar: 头像URL，可为空，最大长度255
-- created_at: 创建时间，不允许为空
-- ========================================
CREATE TABLE IF NOT EXISTS user_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    avatar VARCHAR(255),
    created_at TIMESTAMP NOT NULL
);

-- ========================================
-- 创建新闻表
-- ========================================
-- news_tbl: 存储新闻信息
-- id: 新闻唯一标识，自增主键
-- title: 新闻标题，不允许为空，最大长度255
-- source: 新闻来源，不允许为空，最大长度100
-- summary: 新闻摘要，可为空，最大长度500
-- published_at: 发布时间，不允许为空
-- url: 新闻链接，可为空，最大长度500
-- category: 新闻分类，可为空，最大长度50
-- ========================================
CREATE TABLE IF NOT EXISTS news_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    source VARCHAR(100) NOT NULL,
    summary VARCHAR(500),
    published_at TIMESTAMP NOT NULL,
    url VARCHAR(500),
    category VARCHAR(50)
);

-- ========================================
-- 创建话题表
-- ========================================
-- topic_tbl: 存储话题信息
-- id: 话题唯一标识，自增主键
-- name: 话题名称，不允许为空，最大长度50
-- description: 描述信息，可为空，最大长度255
-- article_count: 关联文章数量，默认为0
-- created_at: 创建时间，不允许为空
-- ========================================
CREATE TABLE IF NOT EXISTS topic_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    article_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL
);

-- ========================================
-- 创建订阅表
-- ========================================
-- subscription_tbl: 存储用户订阅信息
-- id: 订阅唯一标识，自增主键
-- user_id: 外键关联用户表，不允许为空
-- topic_name: 外键关联话题表的名称，不允许为空，最大长度50
-- subscribed_at: 订阅时间，不允许为空
-- ========================================
CREATE TABLE IF NOT EXISTS subscription_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    topic_name VARCHAR(50) NOT NULL,
    subscribed_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user_tbl(id),
    FOREIGN KEY (topic_name) REFERENCES topic_tbl(name)
);

-- ========================================
-- 创建审计日志表
-- ========================================
-- audit_log_tbl: 存储系统操作日志
-- id: 日志唯一标识，自增主键
-- user_id: 外键关联用户表，不允许为空
-- action: 操作类型，不允许为空，最大长度100
-- entity_type: 操作实体类型，可为空，最大长度50
-- entity_id: 操作实体ID，可为空
-- details: 操作详情，可为空，最大长度1000
-- ip_address: 客户端IP地址，可为空，最大长度45
-- created_at: 创建时间，不允许为空
-- ========================================
CREATE TABLE IF NOT EXISTS audit_log_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id BIGINT,
    details VARCHAR(1000),
    ip_address VARCHAR(45),
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user_tbl(id)
);

-- ========================================
-- 插入初始数据
-- ========================================

-- 插入用户数据
-- 管理员用户
INSERT INTO user_tbl (username, email, password, status, avatar, created_at)
VALUES ('admin', 'admin@example.com', 'admin123', 'active', 'https://example.com/avatar_admin.jpg', '2025-01-01 10:00:00');

-- 普通用户
INSERT INTO user_tbl (username, email, password, status, avatar, created_at)
VALUES ('alice', 'alice@example.com', 'user123', 'active', 'https://example.com/avatar_alice.jpg', '2025-01-02 11:00:00');

-- 另一个普通用户
INSERT INTO user_tbl (username, email, password, status, avatar, created_at)
VALUES ('bob', 'bob@example.com', 'user456', 'inactive', 'https://example.com/avatar_bob.jpg', '2025-01-03 12:00:00');

-- 插入新闻数据
INSERT INTO news_tbl (title, source, summary, published_at, url, category)
VALUES ('全球AI技术峰会圆满落幕', '科技日报', '来自全球的AI专家分享了最新研究成果和技术趋势', '2025-01-10 09:00:00', 'https://news.example.com/ai-summit', '科技');

INSERT INTO news_tbl (title, source, summary, published_at, url, category)
VALUES ('新款智能手机发布', '数码前沿', '搭载最新处理器和摄像头技术', '2025-01-12 14:30:00', 'https://news.example.com/new-phone', '数码');

INSERT INTO news_tbl (title, source, summary, published_at, url, category)
VALUES ('健康饮食新研究', '医学周刊', '发现多种食物对心血管有益', '2025-01-15 08:15:00', 'https://news.example.com/healthy-diet', '健康');

-- 插入话题数据
INSERT INTO topic_tbl (name, description, article_count, created_at)
VALUES ('人工智能', '关于AI技术的讨论', 2, '2025-01-05 16:00:00');

INSERT INTO topic_tbl (name, description, article_count, created_at)
VALUES ('健康生活', '健康饮食和运动相关话题', 1, '2025-01-06 17:00:00');

-- 插入订阅数据
-- 管理员订阅人工智能话题
INSERT INTO subscription_tbl (user_id, topic_name, subscribed_at)
SELECT u.id, t.name, '2025-01-06 10:00:00'
FROM user_tbl u, topic_tbl t
WHERE u.email = 'admin@example.com' AND t.name = '人工智能';

-- alice订阅健康生活话题
INSERT INTO subscription_tbl (user_id, topic_name, subscribed_at)
SELECT u.id, t.name, '2025-01-07 11:00:00'
FROM user_tbl u, topic_tbl t
WHERE u.email = 'alice@example.com' AND t.name = '健康生活';

-- bob订阅人工智能话题
INSERT INTO subscription_tbl (user_id, topic_name, subscribed_at)
SELECT u.id, t.name, '2025-01-08 12:00:00'
FROM user_tbl u, topic_tbl t
WHERE u.email = 'bob@example.com' AND t.name = '人工智能';

-- 插入审计日志数据
-- 管理员登录日志
INSERT INTO audit_log_tbl (user_id, action, entity_type, entity_id, details, ip_address, created_at)
SELECT u.id, '用户登录', 'User', u.id, '管理员用户登录系统', '192.168.1.1', '2025-01-06 10:05:00'
FROM user_tbl u
WHERE u.email = 'admin@example.com';

-- 用户创建新闻日志
INSERT INTO audit_log_tbl (user_id, action, entity_type, entity_id, details, ip_address, created_at)
SELECT u.id, '创建新闻', 'News', n.id, '新增新闻：全球AI技术峰会圆满落幕', '192.168.1.2', '2025-01-10 09:05:00'
FROM user_tbl u, news_tbl n
WHERE u.email = 'admin@example.com' AND n.title = '全球AI技术峰会圆满落幕';

-- 用户订阅话题日志
INSERT INTO audit_log_tbl (user_id, action, entity_type, entity_id, details, ip_address, created_at)
SELECT u.id, '订阅话题', 'Topic', t.id, '订阅话题：健康生活', '192.168.1.3', '2025-01-07 11:05:00'
FROM user_tbl u, topic_tbl t
WHERE u.email = 'alice@example.com' AND t.name = '健康生活';