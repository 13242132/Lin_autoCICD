-- ========================================
-- 初始数据库架构
-- 创建所有必要的表并插入初始数据
-- ========================================

-- ========================================
-- 创建用户表
-- ========================================

CREATE TABLE IF NOT EXISTS user_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 用户ID，自增主键
    username VARCHAR(20) NOT NULL, -- 用户名，不允许为空，最大长度20
    email VARCHAR(100) NOT NULL, -- 邮箱，不允许为空，最大长度100
    password VARCHAR(100) NOT NULL, -- 密码，不允许为空，最大长度100
    status VARCHAR(20) NOT NULL, -- 用户状态，默认为"ACTIVE"
    created_at TIMESTAMP NOT NULL -- 创建时间，不允许为空
);

-- ========================================
-- 创建新闻表
-- ========================================

CREATE TABLE IF NOT EXISTS news_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 新闻ID，自增主键
    title VARCHAR(255) NOT NULL, -- 新闻标题，不允许为空，最大长度255
    source VARCHAR(100) NOT NULL, -- 新闻来源，不允许为空，最大长度100
    summary VARCHAR(500), -- 新闻摘要，允许为空，最大长度500
    published_at TIMESTAMP NOT NULL, -- 发布时间，不允许为空
    url VARCHAR(500) -- 新闻链接，允许为空，最大长度500
);

-- ========================================
-- 创建话题表
-- ========================================

CREATE TABLE IF NOT EXISTS topic_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 话题ID，自增主键
    topic_name VARCHAR(50) NOT NULL, -- 话题名称，不允许为空，最大长度50
    description VARCHAR(255) -- 描述，允许为空，最大长度255
);

-- ========================================
-- 创建订阅表
-- ========================================

CREATE TABLE IF NOT EXISTS subscription_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 订阅ID，自增主键
    user_id BIGINT NOT NULL, -- 用户ID，外键关联用户表，不允许为空
    topic_name VARCHAR(50) NOT NULL, -- 话题名称，不允许为空，最大长度50
    subscribed_at TIMESTAMP NOT NULL, -- 订阅时间，不允许为空
    FOREIGN KEY (user_id) REFERENCES user_tbl(id) -- 外键约束，关联用户表的ID
);

-- ========================================
-- 创建审计日志表
-- ========================================

CREATE TABLE IF NOT EXISTS audit_log_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 审计日志ID，自增主键
    user_id BIGINT NOT NULL, -- 用户ID，外键关联用户表，不允许为空
    action VARCHAR(50) NOT NULL, -- 操作动作，不允许为空，最大长度50
    entity_type VARCHAR(50), -- 实体类型，允许为空，最大长度50
    entity_id VARCHAR(50), -- 实体ID，允许为空，最大长度50
    details VARCHAR(1000), -- 操作详情，允许为空，最大长度1000
    created_at TIMESTAMP NOT NULL, -- 创建时间，不允许为空
    FOREIGN KEY (user_id) REFERENCES user_tbl(id) -- 外键约束，关联用户表的ID
);

-- ========================================
-- 插入初始数据
-- ========================================

-- 插入用户数据
INSERT INTO user_tbl (username, email, password, status, created_at)
VALUES
    ('admin', 'admin@example.com', 'admin123', 'ACTIVE', '2025-01-01 10:00:00'),
    ('alice', 'alice@example.com', 'user123', 'ACTIVE', '2025-01-02 11:00:00'),
    ('bob', 'bob@example.com', 'pass456', 'INACTIVE', '2025-01-03 12:00:00');

-- 插入话题数据
INSERT INTO topic_tbl (topic_name, description)
VALUES
    ('Technology', '科技类话题'),
    ('Politics', '政治类话题'),
    ('Sports', '体育类话题');

-- 插入订阅数据
INSERT INTO subscription_tbl (user_id, topic_name, subscribed_at)
SELECT u.id, t.topic_name, '2025-01-04 14:00:00'
FROM user_tbl u, topic_tbl t
WHERE u.email = 'alice@example.com' AND t.topic_name = 'Technology';

INSERT INTO subscription_tbl (user_id, topic_name, subscribed_at)
SELECT u.id, t.topic_name, '2025-01-05 15:00:00'
FROM user_tbl u, topic_tbl t
WHERE u.email = 'bob@example.com' AND t.topic_name IN ('Politics', 'Sports');

-- 插入新闻数据
INSERT INTO news_tbl (title, source, summary, published_at, url)
VALUES
    ('全球科技峰会召开', 'TechNews', '2025全球科技峰会今日在北京召开', '2025-01-04 09:00:00', 'https://example.com/news1'),
    ('国际足球赛事落幕', 'SportsNews', '欧洲杯2025决赛圆满结束', '2025-01-05 17:00:00', 'https://example.com/news2'),
    ('最新环保政策出台', 'GovNews', '国务院发布绿色能源发展指导意见', '2025-01-06 11:00:00', 'https://example.com/news3');

-- 插入审计日志数据
INSERT INTO audit_log_tbl (user_id, action, entity_type, entity_id, details, created_at)
SELECT u.id, 'LOGIN', 'User', CAST(u.id AS VARCHAR), '用户登录系统', '2025-01-06 10:00:00'
FROM user_tbl u
WHERE u.email = 'alice@example.com';

INSERT INTO audit_log_tbl (user_id, action, entity_type, entity_id, details, created_at)
SELECT u.id, 'SUBSCRIBE', 'Topic', t.id, '订阅了话题: ' || t.topic_name, '2025-01-06 11:00:00'
FROM user_tbl u, topic_tbl t
WHERE u.email = 'admin@example.com' AND t.topic_name = 'Politics';