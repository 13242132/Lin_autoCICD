basicInfo: 用户注册 POST /api/auth/register - 创建新用户账户

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
error: "VALIDATION_ERROR"
message: "用户名或邮箱已存在，或密码格式不合法"

apiType: auth_related
controller: AuthController
basePath: /api/auth

---API_SEPARATOR---

basicInfo: 用户登录 POST /api/auth/login - 获取JWT访问令牌

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
category: "可选 - 新闻分类（如科技、体育）"
page: "可选 - 页码，默认为1"
size: "可选 - 每页数量，默认为10"

responseParams:
success:
status_code: 200
response_body:
[
{
id: 1
title: "全球气候峰会召开"
source: "新华社"
publishedAt: "2023-10-15T10:00:00Z"
summary: "多国领导人出席并发表重要讲话..."
}
]
error:
status_code: 500
response_body:
error: "NEWS_AGGREGATION_FAILED"
message: "新闻聚合失败，请检查上游服务"

apiType: entity_related
controller: NewsController
basePath: /api/news

---API_SEPARATOR---

basicInfo: 获取用户订阅主题列表 GET /api/subscriptions - 查询当前用户的新闻主题订阅

requestParams:
(no parameters)

responseParams:
success:
status_code: 200
response_body:
[
{
id: 101
topicName: "科技"
subscribedAt: "2023-10-15T10:00:00Z"
}
]
error:
status_code: 401
response_body:
error: "UNAUTHORIZED"
message: "用户未登录"

apiType: entity_related
controller: SubscriptionController
basePath: /api/subscriptions

---API_SEPARATOR---

basicInfo: 创建/更新用户订阅 PUT /api/subscriptions - 设置用户感兴趣的新闻主题

requestParams:
topicNames: ["科技", "体育", "财经"]

responseParams:
success:
status_code: 200
response_body:
message: "订阅更新成功"
updatedCount: 3
error:
status_code: 400
response_body:
error: "MAX_SUBSCRIPTIONS_EXCEEDED"
message: "最多可订阅5个主题"

apiType: entity_related
controller: SubscriptionController
basePath: /api/subscriptions

---API_SEPARATOR---

basicInfo: 获取推荐新闻列表 GET /api/news/recommended - 根据用户订阅偏好获取个性化推荐新闻

requestParams:
(no parameters)

responseParams:
success:
status_code: 200
response_body:
[
{
id: 201
title: "AI技术新突破"
source: "科技日报"
publishedAt: "2023-10-15T11:30:00Z"
summary: "新一代人工智能模型发布..."
relevanceScore: 0.95
}
]
error:
status_code: 401
response_body:
error: "UNAUTHORIZED"
message: "用户未登录时返回默认热门新闻"

apiType: business_related
controller: RecommendationController
basePath: /api/news/recommended

---API_SEPARATOR---

basicInfo: 获取主题分类列表 GET /api/topics - 获取所有可用的新闻主题分类

requestParams:
(no parameters)

responseParams:
success:
status_code: 200
response_body:
[
{
id: 1
name: "科技"
}
{
id: 2
name: "体育"
}
{
id: 3
name: "财经"
}
]
error:
status_code: 500
response_body:
error: "INTERNAL_ERROR"
message: "服务器内部错误"

apiType: entity_related
controller: TopicController
basePath: /api/topics

---API_SEPARATOR---

basicInfo: 获取用户个人信息 GET /api/users/profile - 获取当前登录用户的个人资料

requestParams:
(no parameters)

responseParams:
success:
status_code: 200
response_body:
id: 1
username: "user123"
email: "user@example.com"
createdAt: "2023-10-15T10:00:00Z"
subscriptionCount: 3
lastLoginAt: "2023-10-15T10:00:00Z"
error:
status_code: 401
response_body:
error: "UNAUTHORIZED"
message: "用户未登录"

apiType: entity_related
controller: UserController
basePath: /api/users

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

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "last_login_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastLoginAt;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";
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

    @Column(name = "summary")
    private String summary;

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

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;
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
    private LocalDateTime subscribedAt = LocalDateTime.now();
}

@Table(name = "audit_log_tbl")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "entity_type", length = 50)
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "details", length = 500)
    private String details;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt = LocalDateTime.now();
}

---ENTITY_LIST_END---