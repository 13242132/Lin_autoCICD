basicInfo: 用户注册 POST /api/auth/register - 创建新用户账号

requestParams:
username: "zhangsan"
password: "P@ssw0rd"
confirmPassword: "P@ssw0rd"

responseParams:
success:
status_code: 201
response_body:
id: 1
username: "zhangsan"
createdAt: "2025-04-05 10:00:00"
error:
status_code: 400
response_body:
error: "REGISTRATION_FAILED"
message: "该用户名已被占用"

apiType: auth_related
controller: AuthController
basePath: /api/auth

---API_SEPARATOR---

basicInfo: 用户登录 POST /api/auth/login - 使用用户名密码登录系统

requestParams:
username: "zhangsan"
password: "P@ssw0rd"

responseParams:
success:
status_code: 200
response_body:
token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.xxxxx"
user:
id: 1
username: "zhangsan"
role: "项目经理"
loginTime: "2025-04-05 10:00:00"
loginIp: "192.168.1.100"
error:
status_code: 401
response_body:
error: "INVALID_CREDENTIALS"
message: "用户名或密码错误"

apiType: auth_related
controller: AuthController
basePath: /api/auth

---API_SEPARATOR---

basicInfo: 创建任务 POST /api/tasks - 创建并分配新任务

requestParams:
title: "完成项目文档"
description: "需在周五前完成"
priority: "medium"
dueDate: "2025-04-12T18:00:00"
assignee: "zhangsan"

responseParams:
success:
status_code: 201
response_body:
id: 101
title: "完成项目文档"
description: "需在周五前完成"
priority: "medium"
dueDate: "2025-04-12T18:00:00"
assignee: "zhangsan"
status: "未开始"
createdAt: "2025-04-05 10:00:00"
error:
status_code: 400
response_body:
error: "TASK_CREATION_FAILED"
message: "请选择优先级"

apiType: entity_related
controller: TaskController
basePath: /api/tasks

---API_SEPARATOR---

basicInfo: 获取任务列表 GET /api/tasks - 查询任务列表（支持按状态过滤）

requestParams:
status: "可选 - 任务状态（未开始、进行中等）"

responseParams:
success:
status_code: 200
response_body:
- id: 101
  title: "完成项目文档"
  priority: "medium"
  dueDate: "2025-04-12T18:00:00"
  assignee: "zhangsan"
  status: "未开始"
  createdAt: "2025-04-05 10:00:00"
- id: 102
  title: "前端开发"
  priority: "high"
  dueDate: "2025-04-10T18:00:00"
  assignee: "lisi"
  status: "进行中"
error:
status_code: 500
response_body:
error: "INTERNAL_ERROR"
message: "服务器错误"

apiType: entity_related
controller: TaskController
basePath: /api/tasks

---API_SEPARATOR---

basicInfo: 更新任务状态 PUT /api/tasks/{taskId}/status - 修改任务当前状态

requestParams:
status: "进行中" // 可选值：未开始、进行中、已完成、已取消

responseParams:
success:
status_code: 200
response_body:
id: 101
title: "完成项目文档"
status: "进行中"
updatedAt: "2025-04-05 11:30:00"
history:
- timestamp: "2025-04-05 10:00:00"
  action: "任务创建"
  from: null
  to: "未开始"
- timestamp: "2025-04-05 11:30:00"
  action: "状态变更为“进行中”"
  from: "未开始"
  to: "进行中"
error:
status_code: 400
response_body:
error: "INVALID_STATUS_TRANSITION"
message: "已完成状态不可变更"

apiType: entity_related
controller: TaskController
basePath: /api/tasks

---API_SEPARATOR---

basicInfo: 发送任务通知 POST /api/business/notify - 向用户推送任务相关通知

requestParams:
userId: 1
type: "task_assigned" // 枚举：task_assigned, status_changed, deadline_reminder
content: "您被分配了一个新任务：“完成项目文档”"

responseParams:
success:
status_code: 200
response_body:
notificationId: "notif_1001"
status: "sent"
sentAt: "2025-04-05 10:30:00"
error:
status_code: 500
response_body:
error: "NOTIFICATION_SEND_FAILED"
message: "通知服务不可用"

apiType: business_related
controller: NotifyController
basePath: /api/business/notify

---API_SEPARATOR---

basicInfo: 获取用户列表 GET /api/users - 查询所有系统用户信息

requestParams:
role: "可选 - 用户角色过滤"

responseParams:
success:
status_code: 200
response_body:
- id: 1
  username: "zhangsan"
  role: "项目经理"
  createdAt: "2025-04-05 10:00:00"
- id: 2
  username: "lisi"
  role: "团队成员"
  createdAt: "2025-04-05 10:10:00"
error:
status_code: 500
response_body:
error: "INTERNAL_ERROR"
message: "数据查询失败"

apiType: entity_related
controller: UserController
basePath: /api/users

---API_SEPARATOR---

basicInfo: 删除用户 DELETE /api/users/{userId} - 删除指定用户账户

requestParams:
// 无请求体，通过路径参数指定用户ID

responseParams:
success:
status_code: 204
response_body: {}
error:
status_code: 404
response_body:
error: "USER_NOT_FOUND"
message: "用户不存在"

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

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "avatar", length = 255)
    private String avatar;

    @Column(name = "role", length = 20)
    private String role;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt = LocalDateTime.now();
}

@Table(name = "task_tbl")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "priority", nullable = false, length = 10)
    private String priority; // 枚举值：low, medium, high

    @Column(name = "due_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime dueDate;

    @Column(name = "assignee", nullable = false, length = 50)
    private String assignee;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // 枚举值：未开始、进行中、已完成、已取消

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedAt;
}

@Table(name = "notification_tbl")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "type", nullable = false, length = 30)
    private String type; // 枚举：task_assigned, status_changed, deadline_reminder

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "sent_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime sentAt;
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
    private String action; // 如：TASK_CREATED, STATUS_UPDATED

    @Column(name = "target_type", nullable = false, length = 20)
    private String targetType; // 如：Task, User

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt = LocalDateTime.now();
}
---ENTITY_LIST_END---