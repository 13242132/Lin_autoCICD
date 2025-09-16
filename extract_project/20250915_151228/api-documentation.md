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
error: "REGISTER_FAILED"
message: "用户名或邮箱已存在，或密码格式不合法"
apiType: auth_related
controller: AuthController
basePath: /api/auth

---API_SEPARATOR---

basicInfo: 用户登录 POST /api/auth/login - 获取JWT令牌
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
message: "用户名/邮箱或密码错误"
apiType: auth_related
controller: AuthController
basePath: /api/auth

---API_SEPARATOR---

basicInfo: 获取新闻列表 GET /api/news - 查询聚合新闻
requestParams:
category: "可选 - 新闻分类"
source: "可选 - 新闻来源"
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
  summary: "全球气候峰会于今日在北京举行..."
error:
status_code: 500
response_body:
error: "FETCH_NEWS_FAILED"
message: "无法从新闻源获取数据"
apiType: entity_related
controller: NewsController
basePath: /api/news

---API_SEPARATOR---

basicInfo: 创建新闻条目 POST /api/news - 添加新的新闻（编辑人员专用）
requestParams:
title: "全球AI技术峰会召开"
source: "科技日报"
publishedAt: "2023-10-15T11:00:00Z"
summary: "本次峰会聚焦人工智能前沿技术..."
url: "https://example.com/news/1"
imageUrl: "https://example.com/image.jpg"
categoryId: 1
responseParams:
success:
status_code: 201
response_body:
id: 2
title: "全球AI技术峰会召开"
source: "科技日报"
publishedAt: "2023-10-15T11:00:00Z"
summary: "本次峰会聚焦人工智能前沿技术..."
url: "https://example.com/news/1"
imageUrl: "https://example.com/image.jpg"
categoryId: 1
createdAt: "2023-10-15T11:05:00Z"
error:
status_code: 400
response_body:
error: "INVALID_DATA"
message: "标题或来源不能为空"
apiType: entity_related
controller: NewsController
basePath: /api/news

---API_SEPARATOR---

basicInfo: 获取用户订阅主题 GET /api/subscriptions - 查询用户的新闻主题订阅
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
message: "用户不存在"
apiType: entity_related
controller: SubscriptionController
basePath: /api/subscriptions

---API_SEPARATOR---

basicInfo: 创建用户订阅 POST /api/subscriptions - 订阅新闻主题
requestParams:
userId: 1001
topicName: "科技"
responseParams:
success:
status_code: 201
response_body:
id: 1
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
basePath: /api/subscriptions

---API_SEPARATOR---

basicInfo: 删除用户订阅 DELETE /api/subscriptions/{id} - 取消订阅特定主题
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
message: "订阅记录不存在"
apiType: entity_related
controller: SubscriptionController
basePath: /api/subscriptions

---API_SEPARATOR---

basicInfo: 获取主题列表 GET /api/topics - 查询所有可用新闻主题
requestParams:
none
responseParams:
success:
status_code: 200
response_body:
- id: 1
  name: "科技"
  description: "科技领域最新动态"
- id: 2
  name: "体育"
  description: "国内外体育赛事"
error:
status_code: 500
response_body:
error: "INTERNAL_ERROR"
message: "服务器内部错误"
apiType: entity_related
controller: TopicController
basePath: /api/topics

---API_SEPARATOR---

basicInfo: 提交用户评论 POST /api/comments - 发表对新闻的评论
requestParams:
newsId: 1
userId: 1001
content: "这个议题非常重要，希望各国能真正落实承诺！"
responseParams:
success:
status_code: 201
response_body:
id: 1
newsId: 1
userId: 1001
content: "这个议题非常重要，希望各国能真正落实承诺！"
createdAt: "2023-10-15T10:10:00Z"
error:
status_code: 400
response_body:
error: "COMMENT_EMPTY"
message: "评论内容不能为空"
apiType: entity_related
controller: CommentController
basePath: /api/comments

---API_SEPARATOR---

basicInfo: 获取新闻详情 GET /api/news/{id} - 查询单条新闻及其关联信息
requestParams:
id: "路径参数 - 新闻ID"
responseParams:
success:
status_code: 200
response_body:
id: 1
title: "全球气候峰会召开"
source: "新华社"
publishedAt: "2023-10-15T10:00:00Z"
summary: "全球气候峰会于今日在北京举行..."
url: "https://example.com/news/1"
imageUrl: "https://example.com/image.jpg"
comments:
  - id: 1
    userId: 1001
    content: "这个议题非常重要，希望各国能真正落实承诺！"
    createdAt: "2023-10-15T10:10:00Z"
likesCount: 25
isLiked: true
error:
status_code: 404
response_body:
error: "NEWS_NOT_FOUND"
message: "新闻不存在"
apiType: entity_related
controller: NewsController
basePath: /api/news

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
    private String status;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;
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

    @Column(name = "published_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime publishedAt;

    @Column(name = "summary", length = 500)
    private String summary;

    @Column(name = "url", length = 500)
    private String url;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;
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

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;
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

    @Column(name = "news_id", nullable = false)
    private Long newsId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;
}

@Table(name = "like_tbl")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "news_id", nullable = false)
    private Long newsId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;
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

    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "details", length = 1000)
    private String details;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;
}

---ENTITY_LIST_END---