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
    email VARCHAR(100) NOT NULL, -- 邮箱地址，不允许为空，最大长度100
    password VARCHAR(100) NOT NULL, -- 密码，不允许为空，最大长度100
    status VARCHAR(20) NOT NULL, -- 用户状态，不允许为空，最大长度20
    created_at TIMESTAMP NOT NULL -- 创建时间，不允许为空
);

-- ========================================
-- 创建新闻表
-- ========================================
CREATE TABLE IF NOT EXISTS news_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 新闻ID，自增主键
    title VARCHAR(255) NOT NULL, -- 标题，不允许为空，最大长度255
    source VARCHAR(100) NOT NULL, -- 来源，不允许为空，最大长度100
    published_at TIMESTAMP NOT NULL, -- 发布时间，不允许为空
    summary VARCHAR(500), -- 摘要，最大长度500
    url VARCHAR(500), -- 新闻链接，最大长度500
    image_url VARCHAR(500), -- 图片链接，最大长度500
    category_id INT, -- 分类ID
    created_at TIMESTAMP NOT NULL -- 创建时间，不允许为空
);

-- ========================================
-- 创建话题表
-- ========================================
CREATE TABLE IF NOT EXISTS topic_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 话题ID，自增主键
    name VARCHAR(50) NOT NULL, -- 话题名称，不允许为空，最大长度50
    description VARCHAR(255), -- 描述，最大长度255
    created_at TIMESTAMP NOT NULL -- 创建时间，不允许为空
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
-- 创建评论表
-- ========================================
CREATE TABLE IF NOT EXISTS comment_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 评论ID，自增主键
    news_id BIGINT NOT NULL, -- 新闻ID，外键关联新闻表，不允许为空
    user_id BIGINT NOT NULL, -- 用户ID，外键关联用户表，不允许为空
    content VARCHAR(1000) NOT NULL, -- 评论内容，不允许为空，最大长度1000
    created_at TIMESTAMP NOT NULL, -- 创建时间，不允许为空
    FOREIGN KEY (news_id) REFERENCES news_tbl(id), -- 外键约束，关联新闻表的ID
    FOREIGN KEY (user_id) REFERENCES user_tbl(id) -- 外键约束，关联用户表的ID
);

-- ========================================
-- 创建点赞表
-- ========================================
CREATE TABLE IF NOT EXISTS like_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 点赞ID，自增主键
    news_id BIGINT NOT NULL, -- 新闻ID，外键关联新闻表，不允许为空
    user_id BIGINT NOT NULL, -- 用户ID，外键关联用户表，不允许为空
    created_at TIMESTAMP NOT NULL, -- 创建时间，不允许为空
    FOREIGN KEY (news_id) REFERENCES news_tbl(id), -- 外键约束，关联新闻表的ID
    FOREIGN KEY (user_id) REFERENCES user_tbl(id) -- 外键约束，关联用户表的ID
);

-- ========================================
-- 创建审计日志表
-- ========================================
CREATE TABLE IF NOT EXISTS audit_log_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- 审计日志ID，自增主键
    user_id BIGINT NOT NULL, -- 用户ID，外键关联用户表，不允许为空
    action VARCHAR(50) NOT NULL, -- 操作动作，不允许为空，最大长度50
    entity_type VARCHAR(50) NOT NULL, -- 实体类型，不允许为空，最大长度50
    entity_id BIGINT, -- 实体ID
    details VARCHAR(1000), -- 操作详情，最大长度1000
    created_at TIMESTAMP NOT NULL, -- 创建时间，不允许为空
    FOREIGN KEY (user_id) REFERENCES user_tbl(id) -- 外键约束，关联用户表的ID
);

-- ========================================
-- 插入初始数据
-- ========================================

-- 插入用户数据（包含管理员和普通用户）
INSERT INTO user_tbl (username, email, password, status, created_at)
VALUES ('admin', 'admin@example.com', 'admin123', 'ACTIVE', '2025-01-01 10:00:00');

INSERT INTO user_tbl (username, email, password, status, created_at)
VALUES ('alice', 'alice@example.com', 'user123', 'ACTIVE', '2025-01-02 11:00:00');

INSERT INTO user_tbl (username, email, password, status, created_at)
VALUES ('bob', 'bob@example.com', 'user456', 'INACTIVE', '2025-01-03 12:00:00');

-- 插入新闻数据
INSERT INTO news_tbl (title, source, published_at, summary, url, image_url, category_id, created_at)
VALUES ('全球AI大会召开', '科技日报', '2025-01-10 09:00:00', '人工智能领域的年度盛会正式开幕', '/news/ai-conference', '/images/ai.jpg', 1, '2025-01-10 08:30:00');

INSERT INTO news_tbl (title, source, published_at, summary, url, image_url, category_id, created_at)
VALUES ('新能源汽车销量增长', '财经新闻', '2025-01-11 14:30:00', '2024年新能源汽车销量同比增长30%', '/news/ev-sales', '/images/ev.png', 2, '2025-01-11 14:00:00');

-- 插入话题数据
INSERT INTO topic_tbl (name, description, created_at)
VALUES ('人工智能', '关于AI、机器学习和深度学习的相关话题', '2025-01-05 10:00:00');

INSERT INTO topic_tbl (name, description, created_at)
VALUES ('新能源汽车', '电动汽车、电池技术及相关政策', '2025-01-06 11:00:00');

-- 插入订阅数据
INSERT INTO subscription_tbl (user_id, topic_name, subscribed_at)
SELECT u.id, '人工智能', '2025-01-08 15:00:00'
FROM user_tbl u
WHERE u.email = 'alice@example.com';

INSERT INTO subscription_tbl (user_id, topic_name, subscribed_at)
SELECT u.id, '新能源汽车', '2025-01-09 16:00:00'
FROM user_tbl u
WHERE u.email = 'bob@example.com';

-- 插入评论数据
INSERT INTO comment_tbl (news_id, user_id, content, created_at)
SELECT n.id, u.id, '这是一条有价值的新闻，值得深入研究。', '2025-01-12 09:30:00'
FROM news_tbl n, user_tbl u
WHERE n.title = '全球AI大会召开' AND u.email = 'alice@example.com';

INSERT INTO comment_tbl (news_id, user_id, content, created_at)
SELECT n.id, u.id, '希望未来能有更多补贴政策出台', '2025-01-13 10:15:00'
FROM news_tbl n, user_tbl u
WHERE n.title = '新能源汽车销量增长' AND u.email = 'bob@example.com';

-- 插入点赞数据
INSERT INTO like_tbl (news_id, user_id, created_at)
SELECT n.id, u.id, '2025-01-12 14:00:00'
FROM news_tbl n, user_tbl u
WHERE n.title = '全球AI大会召开' AND u.email = 'alice@example.com';

INSERT INTO like_tbl (news_id, user_id, created_at)
SELECT n.id, u.id, '2025-01-13 15:00:00'
FROM news_tbl n, user_tbl u
WHERE n.title = '新能源汽车销量增长' AND u.email = 'bob@example.com';

-- 插入审计日志数据
INSERT INTO audit_log_tbl (user_id, action, entity_type, entity_id, details, created_at)
SELECT u.id, 'LOGIN', 'USER', u.id, '用户登录系统', '2025-01-14 08:00:00'
FROM user_tbl u
WHERE u.email = 'admin@example.com';

INSERT INTO audit_log_tbl (user_id, action, entity_type, entity_id, details, created_at)
SELECT u.id, 'COMMENT_CREATED', 'NEWS', c.id, '评论已创建', '2025-01-14 09:00:00'
FROM comment_tbl c
JOIN user_tbl u ON c.user_id = u.id
WHERE u.email = 'alice@example.com';