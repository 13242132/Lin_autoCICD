-- ========================================
-- 初始数据库架构
-- 创建所有必要的表并插入初始数据
-- ========================================

-- ========================================
-- 创建用户表
-- ========================================
-- id BIGINT: 用户ID，自增主键
-- username VARCHAR(20): 用户名，不允许为空，最大长度20
-- email VARCHAR(100): 邮箱，不允许为空，最大长度100
-- password VARCHAR(100): 密码，不允许为空，最大长度100
-- created_at TIMESTAMP: 创建时间，不允许为空
-- last_login_at TIMESTAMP: 最后登录时间，允许为空
-- status VARCHAR(20): 用户状态，不允许为空，默认为"ACTIVE"
-- ========================================
CREATE TABLE IF NOT EXISTS user_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    last_login_at TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);

-- ========================================
-- 创建新闻表
-- ========================================
-- id BIGINT: 新闻ID，自增主键
-- title VARCHAR: 标题，不允许为空
-- source VARCHAR: 来源，不允许为空
-- summary VARCHAR: 摘要，允许为空
-- published_at TIMESTAMP: 发布时间，不允许为空
-- url VARCHAR(500): 新闻链接，允许为空，最大长度500
-- ========================================
CREATE TABLE IF NOT EXISTS news_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR NOT NULL,
    source VARCHAR NOT NULL,
    summary VARCHAR,
    published_at TIMESTAMP NOT NULL,
    url VARCHAR(500)
);

-- ========================================
-- 创建话题表
-- ========================================
-- id BIGINT: 话题ID，自增主键
-- name VARCHAR(50): 话题名称，不允许为空，最大长度50
-- description VARCHAR(255): 描述，允许为空，最大长度255
-- ========================================
CREATE TABLE IF NOT EXISTS topic_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255)
);

-- ========================================
-- 创建订阅表
-- ========================================
-- id BIGINT: 订阅ID，自增主键
-- user_id BIGINT: 用户ID，外键关联用户表，不允许为空
-- topic_name VARCHAR(50): 话题名称，外键关联话题表，不允许为空，最大长度50
-- subscribed_at TIMESTAMP: 订阅时间，不允许为空
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
-- id BIGINT: 日志ID，自增主键
-- user_id BIGINT: 用户ID，外键关联用户表，允许为空
-- action VARCHAR(50): 操作类型，不允许为空，最大长度50
-- entity_type VARCHAR(50): 操作实体类型，允许为空，最大长度50
-- entity_id BIGINT: 实体ID，允许为空
-- details VARCHAR(500): 操作详情，允许为空，最大长度500
-- created_at TIMESTAMP: 创建时间，不允许为空
-- ========================================
CREATE TABLE IF NOT EXISTS audit_log_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50),
    entity_id BIGINT,
    details VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user_tbl(id)
);

-- ========================================
-- 插入初始数据
-- ========================================

-- 插入用户数据
-- 一个管理员用户和两个普通用户
-- ========================================
INSERT INTO user_tbl (username, email, password, created_at)
VALUES ('admin', 'admin@example.com', 'admin123', '2025-01-01 10:00:00');

INSERT INTO user_tbl (username, email, password, created_at)
VALUES ('alice', 'alice@example.com', 'user123', '2025-01-02 11:00:00');

INSERT INTO user_tbl (username, email, password, created_at)
VALUES ('bob', 'bob@example.com', 'user456', '2025-01-03 12:00:00');

-- ========================================
-- 插入话题数据
-- 至少两个话题用于订阅
-- ========================================
INSERT INTO topic_tbl (name, description)
VALUES ('Technology', '科技相关话题');

INSERT INTO topic_tbl (name, description)
VALUES ('Health', '健康相关话题');

-- ========================================
-- 插入订阅数据
-- 模拟用户订阅不同话题
-- ========================================
INSERT INTO subscription_tbl (user_id, topic_name, subscribed_at)
SELECT u.id, t.name, '2025-01-04 13:00:00'
FROM user_tbl u, topic_tbl t
WHERE u.email = 'alice@example.com' AND t.name = 'Technology';

INSERT INTO subscription_tbl (user_id, topic_name, subscribed_at)
SELECT u.id, t.name, '2025-01-05 14:00:00'
FROM user_tbl u, topic_tbl t
WHERE u.email = 'bob@example.com' AND t.name = 'Health';

-- ========================================
-- 插入新闻数据
-- 至少三条新闻，覆盖不同状态和内容
-- ========================================
INSERT INTO news_tbl (title, source, summary, published_at, url)
VALUES ('AI技术突破', '科技日报', '人工智能在医疗领域取得重大突破', '2025-01-06 15:00:00', 'http://example.com/news1');

INSERT INTO news_tbl (title, source, summary, published_at, url)
VALUES ('健康饮食建议', '健康杂志', '专家建议每天摄入五种不同颜色的蔬菜水果', '2025-01-07 16:00:00', 'http://example.com/news2');

INSERT INTO news_tbl (title, source, summary, published_at, url)
VALUES ('新能源汽车发展', '财经新闻', '全球新能源汽车销量持续增长', '2025-01-08 17:00:00', 'http://example.com/news3');

-- ========================================
-- 插入审计日志数据
-- 模拟系统操作日志
-- ========================================
INSERT INTO audit_log_tbl (user_id, action, entity_type, entity_id, details, created_at)
SELECT u.id, 'CREATE', 'USER', u.id, '创建用户: ' || u.username, '2025-01-09 18:00:00'
FROM user_tbl u
WHERE u.email = 'admin@example.com';

INSERT INTO audit_log_tbl (user_id, action, entity_type, entity_id, details, created_at)
SELECT u.id, 'SUBSCRIBE', 'TOPIC', t.id, '订阅话题: ' || t.name, '2025-01-10 19:00:00'
FROM user_tbl u, topic_tbl t
WHERE u.email = 'alice@example.com' AND t.name = 'Technology';

INSERT INTO audit_log_tbl (user_id, action, entity_type, entity_id, details, created_at)
SELECT u.id, 'PUBLISH', 'NEWS', n.id, '发布新闻: ' || n.title, '2025-01-11 20:00:00'
FROM user_tbl u, news_tbl n
WHERE u.email = 'admin@example.com' AND n.title = 'AI技术突破';