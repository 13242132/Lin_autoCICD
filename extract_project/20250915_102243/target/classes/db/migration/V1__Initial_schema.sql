-- ========================================
-- 初始数据库架构
-- 创建所有必要的表并插入初始数据
-- ========================================

-- ========================================
-- 创建用户表
-- ========================================
-- id: 用户ID，自增主键
-- username: 用户名，不允许为空，最大长度20
-- email: 邮箱地址，不允许为空，最大长度100
-- password: 密码，不允许为空，最大长度100
-- status: 用户状态，默认为ACTIVE，不允许为空，最大长度20
-- created_at: 创建时间，不允许为空
-- ========================================
CREATE TABLE IF NOT EXISTS user_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL
);

-- ========================================
-- 创建新闻表
-- ========================================
-- id: 新闻ID，自增主键
-- title: 新闻标题，不允许为空，最大长度255
-- source: 新闻来源，不允许为空，最大长度100
-- summary: 新闻摘要，可为空，最大长度500
-- content: 新闻内容，TEXT类型，可为空
-- published_at: 发布时间，不允许为空
-- url: 新闻链接，可为空，最大长度500
-- ========================================
CREATE TABLE IF NOT EXISTS news_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    source VARCHAR(100) NOT NULL,
    summary VARCHAR(500),
    content TEXT,
    published_at TIMESTAMP NOT NULL,
    url VARCHAR(500)
);

-- ========================================
-- 创建话题表
-- ========================================
-- id: 话题ID，自增主键
-- topic_name: 话题名称，不允许为空，最大长度50
-- description: 话题描述，可为空，最大长度200
-- ========================================
CREATE TABLE IF NOT EXISTS topic_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    topic_name VARCHAR(50) NOT NULL,
    description VARCHAR(200)
);

-- ========================================
-- 创建订阅表
-- ========================================
-- id: 订阅ID，自增主键
-- user_id: 用户ID，外键关联用户表，不允许为空
-- topic_name: 话题名称，不允许为空，最大长度50
-- subscribed_at: 订阅时间，不允许为空
-- ========================================
CREATE TABLE IF NOT EXISTS subscription_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    topic_name VARCHAR(50) NOT NULL,
    subscribed_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user_tbl(id)
);

-- ========================================
-- 创建审计日志表
-- ========================================
-- id: 日志ID，自增主键
-- user_id: 操作用户ID，不允许为空
-- action: 操作动作，不允许为空，最大长度50
-- entity_type: 实体类型，可为空，最大长度50
-- entity_id: 实体ID，可为空
-- details: 操作详情，可为空，最大长度1000
-- created_at: 创建时间，不允许为空
-- ========================================
CREATE TABLE IF NOT EXISTS audit_log_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50),
    entity_id BIGINT,
    details VARCHAR(1000),
    created_at TIMESTAMP NOT NULL
);

-- ========================================
-- 插入初始数据
-- ========================================

-- 插入管理员用户和普通用户
INSERT INTO user_tbl (username, email, password, status, created_at)
VALUES ('admin', 'admin@example.com', 'admin123', 'ACTIVE', '2025-01-01 10:00:00');

INSERT INTO user_tbl (username, email, password, status, created_at)
VALUES ('alice', 'alice@example.com', 'user123', 'ACTIVE', '2025-01-02 11:00:00');

INSERT INTO user_tbl (username, email, password, status, created_at)
VALUES ('bob', 'bob@example.com', 'user456', 'INACTIVE', '2025-01-03 14:00:00');

-- 插入话题数据
INSERT INTO topic_tbl (topic_name, description)
VALUES ('TECHNOLOGY', '科技新闻');

INSERT INTO topic_tbl (topic_name, description)
VALUES ('SPORTS', '体育新闻');

INSERT INTO topic_tbl (topic_name, description)
VALUES ('POLITICS', '政治新闻');

-- 插入用户订阅数据
INSERT INTO subscription_tbl (user_id, topic_name, subscribed_at)
SELECT u.id, 'TECHNOLOGY', '2025-01-04 16:00:00'
FROM user_tbl u
WHERE u.username = 'alice';

INSERT INTO subscription_tbl (user_id, topic_name, subscribed_at)
SELECT u.id, 'SPORTS', '2025-01-05 09:00:00'
FROM user_tbl u
WHERE u.username = 'bob';

-- 插入新闻数据
INSERT INTO news_tbl (title, source, summary, content, published_at, url)
VALUES (
    '全球科技大会在旧金山举行',
    'TechNews',
    '今年全球科技大会吸引了超过5万名参会者',
    '详细内容包括苹果、谷歌、微软等公司发布了最新的产品和技术趋势...',
    '2025-01-05 12:00:00',
    'https://example.com/technews1'
);

INSERT INTO news_tbl (title, source, summary, content, published_at, url)
VALUES (
    '世界杯决赛将在卡塔尔举行',
    'SportsNews',
    '国际足联宣布世界杯决赛日期和场地',
    '本届世界杯将采用全新的VAR技术以提高比赛公平性...',
    '2025-01-06 18:00:00',
    'https://example.com/sportsnews1'
);

-- 插入审计日志数据
INSERT INTO audit_log_tbl (user_id, action, entity_type, entity_id, details, created_at)
SELECT u.id, 'LOGIN', 'USER', u.id, '用户成功登录系统', '2025-01-07 08:00:00'
FROM user_tbl u
WHERE u.username = 'admin';

INSERT INTO audit_log_tbl (user_id, action, entity_type, entity_id, details, created_at)
SELECT u.id, 'CREATE_NEWS', 'NEWS', 1, '创建了一篇新的科技新闻', '2025-01-08 10:30:00'
FROM user_tbl u
WHERE u.username = 'alice';