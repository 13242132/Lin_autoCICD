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
error: "REGISTRATION_FAILED"
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
basicInfo: 获取新闻列表 GET /api/news - 查询聚合新闻内容
requestParams:
category: "可选 - 新闻分类（如科技、体育）"
page: "可选 - 页码，默认1"
size: "可选 - 每页数量，默认10"
responseParams:
success:
status_code: 200
response_body:
- id: 1
  title: "全球气候峰会召开"
  source: "新华社"
  publishedAt: "2023-10-15T10:00:00Z"
  summary: "多国领导人出席并发表重要讲话..."
error:
status_code: 500
response_body:
error: "FETCH_NEWS_FAILED"
message: "无法从上游服务获取新闻数据"
apiType: entity_related
controller: NewsController
basePath: /api/news
---API_SEPARATOR---
basicInfo: 获取用户订阅主题列表 GET /api/users/{userId}/subscriptions - 查询用户订阅的新闻主题
requestParams:
userId: "路径参数 - 用户ID"
responseParams:
success:
status_code: 200
response_body:
- id: 101
  topicName: "科技"
  subscribedAt: "2023-10-15T10:00:00Z"
error:
status_code: 404
response_body:
error: "USER_NOT_FOUND"
message: "用户不存在"
apiType: entity_related
controller: SubscriptionController
basePath: /api/users/{userId}/subscriptions
---API_SEPARATOR---
basicInfo: 创建用户订阅关系 POST /api/users/{userId}/subscriptions - 添加新的新闻主题订阅
requestParams:
userId: "路径参数 - 用户ID"
topicName: "要订阅的主题名称"
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
message: "最多只能订阅5个主题"
apiType: entity_related
controller: SubscriptionController
basePath: /api/users/{userId}/subscriptions
---API_SEPARATOR---
basicInfo: 删除用户订阅关系 DELETE /api/users/{userId}/subscriptions/{subscriptionId} - 取消特定主题订阅
requestParams:
userId: "路径参数 - 用户ID"
subscriptionId: "路径参数 - 订阅记录ID"
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
basePath: /api/users/{userId}/subscriptions/{subscriptionId}
---API_SEPARATOR---
basicInfo: 获取推荐新闻列表 GET /api/recommendations - 基于用户订阅偏好获取个性化推荐
requestParams:
userId: "可选 - 用户ID，未登录时返回热门新闻"
responseParams:
success:
status_code: 200
response_body:
- id: 1
  title: "全球AI技术峰会召开"
  source: "科技日报"
  publishedAt: "2023-10-15T08:30:00Z"
  relevanceScore: 0.95
error:
status_code: 500
response_body:
error: "RECOMMENDATION_ENGINE_ERROR"
message: "推荐算法服务异常"
apiType: business_related
controller: RecommendationController
basePath: /api/recommendations
---API_SEPARATOR---
basicInfo: 获取系统健康状态 GET /api/health - 检查服务运行状况
requestParams: {}
responseParams:
success:
status_code: 200
response_body:
status: "UP"
timestamp: "2023-10-15T10:00:00Z"
dependencies:
  newsApi: "UP"
  authService: "UP"
error:
status_code: 503
response_body:
status: "DOWN"
failedDependency: "newsApi"
apiType: business_related
controller: HealthController
basePath: /api/health
---API_SEPARATOR---
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

    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and Setters
}

@Table(name = "news_tbl")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "source", nullable = false, length = 100)
    private String source;

    @Column(name = "summary", length = 500)
    private String summary;

    @Column(name = "published_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime publishedAt;

    @Column(name = "url", length = 500)
    private String url;

    // Getters and Setters
}

@Table(name = "topic_tbl")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "topic_name", nullable = false, length = 50)
    private String topicName;

    @Column(name = "description", length = 255)
    private String description;

    // Getters and Setters
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

    // Getters and Setters
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

    @Column(name = "entity_id", length = 50)
    private String entityId;

    @Column(name = "details", length = 1000)
    private String details;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and Setters
}
---ENTITY_LIST_END---