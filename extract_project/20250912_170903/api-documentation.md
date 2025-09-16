basicInfo: 用户注册 POST /api/auth/register - 创建新用户
requestParams:
username: "user123"
email: "user@example.com"
password: "Pass1234"
responseParams:
success:
status_code: 201
response_body:
id: 1
username: "user123"
email: "user@example.com"
createdAt: "2023-10-15T10:00:00Z"
error:
status_code: 400
response_body:
error: "REGISTRATION_FAILED"
message: "用户名或邮箱已存在"
apiType: auth_related
controller: AuthController
basePath: /api/auth

---API_SEPARATOR---

basicInfo: 用户登录 POST /api/auth/login - 获取访问令牌
requestParams:
usernameOrEmail: "user123"
password: "Pass1234"
responseParams:
success:
status_code: 200
response_body:
token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
userId: 1
username: "user123"
expiresAt: "2023-10-15T18:00:00Z"
error:
status_code: 401
response_body:
error: "INVALID_CREDENTIALS"
message: "用户名或密码错误"
apiType: auth_related
controller: AuthController
basePath: /api/auth

---API_SEPARATOR---

basicInfo: 查询新闻列表 GET /api/news - 获取聚合新闻内容
requestParams:
category: "可选 - 新闻分类"
page: "可选 - 页码，默认1"
size: "可选 - 每页数量，默认10"
responseParams:
success:
status_code: 200
response_body:
items:
- id: 1
  title: "全球气候峰会召开"
  source: "新华社"
  publishedAt: "2023-10-15T10:00:00Z"
  summary: "全球气候峰会于今日在北京举行..."
total: 100
page: 1
size: 10
error:
status_code: 500
response_body:
error: "NEWS_FETCH_FAILED"
message: "无法从第三方源获取新闻"
apiType: entity_related
controller: NewsController
basePath: /api/news

---API_SEPARATOR---

basicInfo: 获取用户订阅主题 GET /api/users/{userId}/subscriptions - 查询用户订阅偏好
requestParams:
userId: "路径参数 - 用户ID"
responseParams:
success:
status_code: 200
response_body:
userId: 1001
topics:
- topicName: "科技"
  subscribedAt: "2023-10-15T10:00:00Z"
- topicName: "体育"
  subscribedAt: "2023-10-15T10:00:00Z"
total: 2
error:
status_code: 404
response_body:
error: "USER_NOT_FOUND"
message: "用户不存在"
apiType: entity_related
controller: SubscriptionController
basePath: /api/users/{userId}/subscriptions

---API_SEPARATOR---

basicInfo: 创建用户订阅 POST /api/users/{userId}/subscriptions - 添加新闻主题订阅
requestParams:
userId: "路径参数 - 用户ID"
topicName: "请求体 - 主题名称"
responseParams:
success:
status_code: 201
response_body:
id: 101
userId: 1001
topicName: "科技"
subscribedAt: "2023-10-15T10:00:00Z"
error:
status_code: 400
response_body:
error: "SUBSCRIPTION_LIMIT_EXCEEDED"
message: "最多可订阅5个主题"
apiType: entity_related
controller: SubscriptionController
basePath: /api/users/{userId}/subscriptions

---API_SEPARATOR---

basicInfo: 删除用户订阅 DELETE /api/users/{userId}/subscriptions/{topicName} - 取消新闻主题订阅
requestParams:
userId: "路径参数 - 用户ID"
topicName: "路径参数 - 主题名称"
responseParams:
success:
status_code: 204
response_body: {}
error:
status_code: 404
response_body:
error: "SUBSCRIPTION_NOT_FOUND"
message: "订阅记录不存在"
apiType: entity_related
controller: SubscriptionController
basePath: /api/users/{userId}/subscriptions/{topicName}

---API_SEPARATOR---

basicInfo: 获取热门主题 GET /api/topics/hot - 获取当前热门新闻主题
requestParams:
limit: "可选 - 返回数量，默认5"
responseParams:
success:
status_code: 200
response_body:
topics:
- topicName: "科技"
  newsCount: 25
  trendScore: 95.5
- topicName: "体育"
  newsCount: 18
  trendScore: 87.2
error:
status_code: 500
response_body:
error: "INTERNAL_ERROR"
message: "服务器内部错误"
apiType: business_related
controller: TopicController
basePath: /api/topics/hot

---API_SEPARATOR---

basicInfo: 获取用户个人资料 GET /api/users/{userId}/profile - 查询用户详细信息
requestParams:
userId: "路径参数 - 用户ID"
responseParams:
success:
status_code: 200
response_body:
id: 1001
username: "user123"
email: "user@example.com"
avatar: "https://example.com/avatar/1001.jpg"
status: "active"
createdAt: "2023-10-15T10:00:00Z"
subscriptionCount: 6
lastLoginAt: "2023-10-15T10:00:00Z"
error:
status_code: 404
response_body:
error: "USER_NOT_FOUND"
message: "用户不存在"
apiType: entity_related
controller: UserController
basePath: /api/users/{userId}/profile

---API_SEPARATOR---

basicInfo: 更新用户阅读历史 POST /api/users/{userId}/history - 记录用户浏览的新闻
requestParams:
userId: "路径参数 - 用户ID"
newsId: "请求体 - 新闻ID"
readAt: "请求体 - 阅读时间"
responseParams:
success:
status_code: 201
response_body:
id: 1001
userId: 1001
newsId: 1
title: "全球气候峰会召开"
source: "新华社"
readAt: "2023-10-15T10:00:00Z"
error:
status_code: 400
response_body:
error: "INVALID_NEWS_ID"
message: "新闻ID无效"
apiType: entity_related
controller: UserHistoryController
basePath: /api/users/{userId}/history

---API_SEPARATOR---

basicInfo: 获取用户阅读历史 GET /api/users/{userId}/history - 查询用户浏览过的新闻记录
requestParams:
userId: "路径参数 - 用户ID"
page: "可选 - 页码，默认1"
size: "可选 - 每页数量，默认10"
responseParams:
success:
status_code: 200
response_body:
items:
- id: 1001
  userId: 1001
  newsId: 1
  title: "全球气候峰会召开"
  source: "新华社"
  readAt: "2023-10-15T10:00:00Z"
total: 50
page: 1
size: 10
error:
status_code: 404
response_body:
error: "USER_NOT_FOUND"
message: "用户不存在"
apiType: entity_related
controller: UserHistoryController
basePath: /api/users/{userId}/history

---ENTITY_LIST_START---

@Table(name = "user_tbl")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = false, length = 20)
    private String username;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "avatar", length = 255)
    private String avatar;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    @Column(name = "last_login_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastLoginAt;
}

@Table(name = "news_tbl")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "source", nullable = false)
    private String source;

    @Column(name = "summary", length = 500)
    private String summary;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "published_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime publishedAt;

    @Column(name = "url", length = 500)
    private String url;
}

@Table(name = "topic_tbl")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "topic_name", nullable = false, length = 50)
    private String topicName;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "news_count")
    private Integer newsCount;

    @Column(name = "trend_score", precision = 5, scale = 2)
    private BigDecimal trendScore;
}

@Table(name = "subscription_tbl")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "topic_name", nullable = false, length = 50)
    private String topicName;

    @Column(name = "subscribed_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime subscribedAt;
}

@Table(name = "comment_tbl")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "news_id", nullable = false)
    private Long newsId;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    @Column(name = "parent_comment_id")
    private Long parentCommentId;
}

@Table(name = "like_tbl")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "news_id", nullable = false)
    private Long newsId;

    @Column(name = "liked_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime likedAt;
}

@Table(name = "audit_log_tbl")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "entity_type", length = 50)
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "details", length = 1000)
    private String details;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;
}

@Table(name = "user_history_tbl")
public class UserHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "news_id", nullable = false)
    private Long newsId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "source", nullable = false)
    private String source;

    @Column(name = "read_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime readAt;
}

---ENTITY_LIST_END---