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
basicInfo: 用户登录 POST /api/auth/login - 验证凭据并获取JWT令牌
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
email: "user@example.com"
error:
status_code: 401
response_body:
error: "INVALID_CREDENTIALS"
message: "用户名/邮箱或密码错误"
apiType: auth_related
controller: AuthController
basePath: /api/auth
---API_SEPARATOR---
basicInfo: 获取新闻列表 GET /api/news - 查询聚合新闻内容
requestParams:
category: "可选 - 新闻分类（如科技、体育）"
page: "可选 - 页码，默认为1"
size: "可选 - 每页数量，默认为10"
responseParams:
success:
status_code: 200
response_body:
- id: 1
  title: "全球气候峰会召开"
  source: "新华社"
  publishedAt: "2023-10-15T10:00:00Z"
  summary: "全球气候峰会于今日在北京举行..."
error:
status_code: 500
response_body:
error: "NEWS_AGGREGATION_FAILED"
message: "无法从第三方源获取新闻数据"
apiType: entity_related
controller: NewsController
basePath: /api/news
---API_SEPARATOR---
basicInfo: 获取用户订阅主题列表 GET /api/subscriptions - 查询用户已订阅的新闻主题
requestParams:
userId: "必填 - 用户ID"
responseParams:
success:
status_code: 200
response_body:
- topicName: "科技"
  subscribedAt: "2023-10-15T10:00:00Z"
- topicName: "体育"
  subscribedAt: "2023-10-15T10:05:00Z"
error:
status_code: 404
response_body:
error: "USER_NOT_FOUND"
message: "指定用户不存在"
apiType: entity_related
controller: SubscriptionController
basePath: /api/subscriptions
---API_SEPARATOR---
basicInfo: 创建用户主题订阅 POST /api/subscriptions - 添加新的新闻主题订阅
requestParams:
userId: 1001
topicName: "科技"
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
message: "用户最多可订阅5个主题"
apiType: entity_related
controller: SubscriptionController
basePath: /api/subscriptions
---API_SEPARATOR---
basicInfo: 删除用户主题订阅 DELETE /api/subscriptions/{id} - 取消特定主题订阅
requestParams:
id: "路径参数 - 订阅记录ID"
responseParams:
success:
status_code: 204
response_body: {}
error:
status_code: 404
response_body:
error: "SUBSCRIPTION_NOT_FOUND"
message: "未找到对应的订阅记录"
apiType: entity_related
controller: SubscriptionController
basePath: /api/subscriptions
---API_SEPARATOR---
basicInfo: 获取推荐新闻列表 GET /api/recommendations - 根据用户订阅偏好获取个性化推荐
requestParams:
userId: "必填 - 用户ID"
responseParams:
success:
status_code: 200
response_body:
- id: 201
  title: "AI技术新突破"
  source: "科技日报"
  publishedAt: "2023-10-15T09:30:00Z"
  topic: "科技"
- id: 202
  title: "中国男篮晋级世界杯四强"
  source: "腾讯体育"
  publishedAt: "2023-10-15T08:45:00Z"
  topic: "体育"
error:
status_code: 401
response_body:
error: "UNAUTHORIZED"
message: "请先登录以获取个性化推荐"
apiType: business_related
controller: RecommendationController
basePath: /api/business/recommendations
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

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt = LocalDateTime.now();
}
---ENTITY_LIST_END---