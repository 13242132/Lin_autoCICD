-- ========================================
-- 初始数据库架构
-- 创建所有必要的表并插入初始数据
-- ========================================

-- ========================================
-- 创建用户表
-- ========================================
-- 表名：user_tbl
-- 字段说明：
-- id: 用户ID，BIGINT，NOT NULL，自增主键
-- username: 用户名，VARCHAR(20)，NOT NULL
-- email: 电子邮箱，VARCHAR(100)，NOT NULL
-- password: 密码，VARCHAR(100)，NOT NULL
-- avatar: 头像URL，VARCHAR(255)，可空
-- status: 用户状态，VARCHAR(20)，NOT NULL
-- created_at: 创建时间，TIMESTAMP，NOT NULL
-- last_login_at: 最后登录时间，TIMESTAMP，可空
-- ========================================
CREATE TABLE IF NOT EXISTS user_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    avatar VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    last_login_at TIMESTAMP
);

-- ========================================
-- 创建新闻表
-- ========================================
-- 表名：news_tbl
-- 字段说明：
-- id: 新闻ID，BIGINT，NOT NULL，自增主键
-- title: 标题，VARCHAR，NOT NULL
-- source: 来源，VARCHAR，NOT NULL
-- summary: 摘要，VARCHAR(500)，可空
-- content: 内容，TEXT，可空
-- published_at: 发布时间，TIMESTAMP，NOT NULL
-- url: 原文链接，VARCHAR(500)，可空
-- ========================================
CREATE TABLE IF NOT EXISTS news_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR NOT NULL,
    source VARCHAR NOT NULL,
    summary VARCHAR(500),
    content TEXT,
    published_at TIMESTAMP NOT NULL,
    url VARCHAR(500)
);

-- ========================================
-- 创建话题表
-- ========================================
-- 表名：topic_tbl
-- 字段说明：
-- id: 话题ID，BIGINT，NOT NULL，自增主键
-- topic_name: 话题名称，VARCHAR(50)，NOT NULL
-- description: 描述，VARCHAR(200)，可空
-- news_count: 关联新闻数量，INTEGER，可空
-- trend_score: 热度评分，DECIMAL(5,2)，可空
-- ========================================
CREATE TABLE IF NOT EXISTS topic_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    topic_name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    news_count INTEGER,
    trend_score DECIMAL(5,2)
);

-- ========================================
-- 创建订阅表
-- ========================================
-- 表名：subscription_tbl
-- 字段说明：
-- id: 订阅ID，BIGINT，NOT NULL，自增主键
-- user_id: 用户ID，BIGINT，NOT NULL
-- topic_name: 话题名称，VARCHAR(50)，NOT NULL
-- subscribed_at: 订阅时间，TIMESTAMP，NOT NULL
-- 外键约束：
-- user_id -> user_tbl.id
-- ========================================
CREATE TABLE IF NOT EXISTS subscription_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    topic_name VARCHAR(50) NOT NULL,
    subscribed_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user_tbl(id)
);

-- ========================================
-- 创建评论表
-- ========================================
-- 表名：comment_tbl
-- 字段说明：
-- id: 评论ID，BIGINT，NOT NULL，自增主键
-- user_id: 用户ID，BIGINT，NOT NULL
-- news_id: 新闻ID，BIGINT，NOT NULL
-- content: 评论内容，VARCHAR(1000)，NOT NULL
-- created_at: 创建时间，TIMESTAMP，NOT NULL
-- parent_comment_id: 父评论ID，BIGINT，可空
-- 外键约束：
-- user_id -> user_tbl.id
-- news_id -> news_tbl.id
-- parent_comment_id -> comment_tbl.id
-- ========================================
CREATE TABLE IF NOT EXISTS comment_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    news_id BIGINT NOT NULL,
    content VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    parent_comment_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES user_tbl(id),
    FOREIGN KEY (news_id) REFERENCES news_tbl(id),
    FOREIGN KEY (parent_comment_id) REFERENCES comment_tbl(id)
);

-- ========================================
-- 创建点赞表
-- ========================================
-- 表名：like_tbl
-- 字段说明：
-- id: 点赞ID，BIGINT，NOT NULL，自增主键
-- user_id: 用户ID，BIGINT，NOT NULL
-- news_id: 新闻ID，BIGINT，NOT NULL
-- liked_at: 点赞时间，TIMESTAMP，NOT NULL
-- 外键约束：
-- user_id -> user_tbl.id
-- news_id -> news_tbl.id
-- ========================================
CREATE TABLE IF NOT EXISTS like_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    news_id BIGINT NOT NULL,
    liked_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user_tbl(id),
    FOREIGN KEY (news_id) REFERENCES news_tbl(id)
);

-- ========================================
-- 创建审计日志表
-- ========================================
-- 表名：audit_log_tbl
-- 字段说明：
-- id: 日志ID，BIGINT，NOT NULL，自增主键
-- user_id: 用户ID，BIGINT，NOT NULL
-- action: 操作动作，VARCHAR(50)，NOT NULL
-- entity_type: 实体类型，VARCHAR(50)，可空
-- entity_id: 实体ID，BIGINT，可空
-- details: 详细信息，VARCHAR(1000)，可空
-- ip_address: IP地址，VARCHAR(45)，可空
-- created_at: 创建时间，TIMESTAMP，NOT NULL
-- 外键约束：
-- user_id -> user_tbl.id
-- ========================================
CREATE TABLE IF NOT EXISTS audit_log_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50),
    entity_id BIGINT,
    details VARCHAR(1000),
    ip_address VARCHAR(45),
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user_tbl(id)
);

-- ========================================
-- 创建用户浏览历史表
-- ========================================
-- 表名：user_history_tbl
-- 字段说明：
-- id: 历史记录ID，BIGINT，NOT NULL，自增主键
-- user_id: 用户ID，BIGINT，NOT NULL
-- news_id: 新闻ID，BIGINT，NOT NULL
-- title: 新闻标题，VARCHAR，NOT NULL
-- source: 来源，VARCHAR，NOT NULL
-- read_at: 阅读时间，TIMESTAMP，NOT NULL
-- 外键约束：
-- user_id -> user_tbl.id
-- news_id -> news_tbl.id
-- ========================================
CREATE TABLE IF NOT EXISTS user_history_tbl (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    news_id BIGINT NOT NULL,
    title VARCHAR NOT NULL,
    source VARCHAR NOT NULL,
    read_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user_tbl(id),
    FOREIGN KEY (news_id) REFERENCES news_tbl(id)
);

-- ========================================
-- 插入初始数据
-- ========================================

-- 插入用户数据
INSERT INTO user_tbl (username, email, password, avatar, status, created_at, last_login_at) VALUES
('admin', 'admin@example.com', 'admin123', 'https://example.com/avatar_admin.jpg', 'ACTIVE', '2025-01-01 10:00:00', '2025-01-05 14:30:00'),
('user1', 'user1@example.com', 'user123', 'https://example.com/avatar1.jpg', 'ACTIVE', '2025-01-02 11:00:00', '2025-01-06 09:15:00'),
('user2', 'user2@example.com', 'user456', 'https://example.com/avatar2.jpg', 'INACTIVE', '2025-01-03 12:00:00', '2025-01-04 16:45:00');

-- 插入新闻数据
INSERT INTO news_tbl (title, source, summary, content, published_at, url) VALUES
('科技新突破：AI芯片性能翻倍', '科技日报', '新一代AI芯片发布，性能提升200%', '详细内容描述新一代AI芯片的技术参数和性能优势...', '2025-01-01 09:00:00', 'https://news.example.com/ai-chip'),
('全球气候峰会达成新协议', '环保新闻', '各国签署气候行动新协议', '详细内容描述全球气候峰会的讨论成果和未来行动计划...', '2025-01-02 10:30:00', 'https://news.example.com/climate-summit'),
('股市大涨：A股创三年新高', '财经新闻', '受政策利好影响，股市持续上涨', '详细内容分析股市上涨的原因及未来走势预测...', '2025-01-03 14:00:00', 'https://news.example.com/stock-market');

-- 插入话题数据
INSERT INTO topic_tbl (topic_name, description, news_count, trend_score) VALUES
('人工智能', '关于AI技术发展和应用的讨论', 10, 8.75),
('气候变化', '全球变暖与环境保护相关话题', 7, 7.50),
('股市行情', '股票市场走势和投资分析', 5, 6.25);

-- 插入订阅数据
INSERT INTO subscription_tbl (user_id, topic_name, subscribed_at)
SELECT u.id, '人工智能', '2025-01-04 10:00:00'
FROM user_tbl u
WHERE u.username = 'admin';

INSERT INTO subscription_tbl (user_id, topic_name, subscribed_at)
SELECT u.id, '气候变化', '2025-01-05 11:00:00'
FROM user_tbl u
WHERE u.username = 'user1';

-- 插入评论数据
INSERT INTO comment_tbl (user_id, news_id, content, created_at)
SELECT u.id, n.id, '非常有深度的报道，期待更多类似内容', '2025-01-05 13:00:00'
FROM user_tbl u, news_tbl n
WHERE u.username = 'user1' AND n.title = '科技新突破：AI芯片性能翻倍';

INSERT INTO comment_tbl (user_id, news_id, content, created_at)
SELECT u.id, n.id, '希望政策能真正落实', '2025-01-06 09:30:00'
FROM user_tbl u, news_tbl n
WHERE u.username = 'user2' AND n.title = '全球气候峰会达成新协议';

-- 插入点赞数据
INSERT INTO like_tbl (user_id, news_id, liked_at)
SELECT u.id, n.id, '2025-01-05 14:00:00'
FROM user_tbl u, news_tbl n
WHERE u.username = 'user1' AND n.title = '科技新突破：AI芯片性能翻倍';

-- 插入审计日志数据
INSERT INTO audit_log_tbl (user_id, action, entity_type, entity_id, details, ip_address, created_at)
SELECT u.id, 'LOGIN', 'USER', u.id, '用户登录系统', '192.168.1.1', '2025-01-05 14:30:00'
FROM user_tbl u
WHERE u.username = 'admin';

-- 插入用户浏览历史数据
INSERT INTO user_history_tbl (user_id, news_id, title, source, read_at)
SELECT u.id, n.id, n.title, n.source, '2025-01-05 15:00:00'
FROM user_tbl u, news_tbl n
WHERE u.username = 'user1' AND n.title = '科技新突破：AI芯片性能翻倍';