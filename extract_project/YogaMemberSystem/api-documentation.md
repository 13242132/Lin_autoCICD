根据提供的需求文档和原型HTML，我推断出以下实体定义并生成相应的接口文档：

// 第1个接口
basicInfo: 会员注册 POST /api/auth/register - 新用户注册
requestParams:
phone: "13800001111"
password: "abc123"
confirmPassword: "abc123"
code: "123456"
agreeTerms: true
responseParams:
// 注意：status_code 是 HTTP 状态码（元数据），不在 JSON 响应体中。
// 成功时直接返回 response_body 定义的实体结构。
success:
  status_code: 201
  response_body:
    id: 1
    phone: "13800001111"
    nickname: "用户13800001111"
    status: "正常"
    createdAt: "2024-10-15 10:00:00"
error:
  status_code: 400
  response_body:
    error: "REGISTER_FAILED"
    message: "注册失败，手机号已存在"
apiType: auth_related
controller: AuthController
basePath: /api/auth

---API_SEPARATOR---

// 第2个接口
basicInfo: 会员登录 POST /api/auth/login - 用户登录
requestParams:
phone: "13800001111"
password: "abc123"
rememberMe: false
responseParams:
// 注意：status_code 是 HTTP 状态码（元数据），不在 JSON 响应体中。
// 成功时直接返回 response_body 定义的实体结构。
success:
  status_code: 200
  response_body:
    token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    member:
      id: 1
      phone: "13800001111"
      nickname: "张三"
      status: "正常"
error:
  status_code: 401
  response_body:
    error: "LOGIN_FAILED"
    message: "手机号或密码错误"
apiType: auth_related
controller: AuthController
basePath: /api/auth

---API_SEPARATOR---

// 第3个接口
basicInfo: 获取课程列表 GET /api/courses - 查询可预约课程
requestParams:
date: "可选 - 筛选日期，格式：yyyy-MM-dd"
keyword: "可选 - 搜索关键词"
responseParams:
// 注意：status_code 是 HTTP 状态码（元数据），不在 JSON 响应体中。
// 成功时直接返回 response_body 定义的实体结构。
success:
  status_code: 200
  response_body:
    - id: 1001
      name: "基础瑜伽入门"
      coach: "张老师"
      startTime: "2024-10-16 18:00:00"
      endTime: "2024-10-16 19:00:00"
      totalSlots: 10
      availableSlots: 8
      status: "可预约"
    - id: 1002
      name: "流瑜伽进阶"
      coach: "李老师"
      startTime: "2024-10-16 19:30:00"
      endTime: "2024-10-16 20:30:00"
      totalSlots: 10
      availableSlots: 0
      status: "已满员"
error:
  status_code: 500
  response_body:
    error: "INTERNAL_ERROR"
    message: "服务器内部错误"
apiType: entity_related
controller: CourseController
basePath: /api/courses

---API_SEPARATOR---

// 第4个接口
basicInfo: 课程预约 POST /api/appointments - 预约课程
requestParams:
courseId: 1001
memberId: 2001
appointmentTime: "2024-10-16 18:00:00"
responseParams:
// 注意：status_code 是 HTTP 状态码（元数据），不在 JSON 响应体中。
// 成功时直接返回 response_body 定义的实体结构。
success:
  status_code: 201
  response_body:
    id: 1
    courseId: 1001
    memberId: 2001
    appointmentTime: "2024-10-16 18:00:00"
    status: "已预约"
    createdAt: "2024-10-15 14:30:00"
error:
  status_code: 400
  response_body:
    error: "APPOINTMENT_FAILED"
    message: "预约失败，课程已满或已预约"
apiType: entity_related
controller: AppointmentController
basePath: /api/appointments

---API_SEPARATOR---

// 第5个接口
basicInfo: 获取我的预约 GET /api/appointments/my - 查询当前用户的预约
requestParams:
status: "可选 - 预约状态筛选"
responseParams:
// 注意：status_code 是 HTTP 状态码（元数据），不在 JSON 响应体中。
// 成功时直接返回 response_body 定义的实体结构。
success:
  status_code: 200
  response_body:
    - id: 1
      courseName: "流瑜伽"
      courseTime: "2024-10-16 18:00:00"
      status: "已预约"
      createdAt: "2024-10-15 14:30:00"
    - id: 2
      courseName: "空中瑜伽"
      courseTime: "2024-10-18 19:30:00"
      status: "已预约"
      createdAt: "2024-10-15 15:20:00"
error:
  status_code: 401
  response_body:
    error: "UNAUTHORIZED"
    message: "未授权访问"
apiType: entity_related
controller: AppointmentController
basePath: /api/appointments

---API_SEPARATOR---

// 第6个接口
basicInfo: 课程签到 POST /api/checkins - 会员课程签到
requestParams:
courseId: 1001
memberId: 2001
checkInTime: "2024-10-15 17:55:00"
responseParams:
// 注意：status_code 是 HTTP 状态码（元数据），不在 JSON 响应体中。
// 成功时直接返回 response_body 定义的实体结构。
success:
  status_code: 200
  response_body:
    id: 1
    courseId: 1001
    memberId: 2001
    checkInTime: "2024-10-15 17:55:00"
    status: "已签到"
error:
  status_code: 400
  response_body:
    error: "CHECKIN_FAILED"
    message: "签到失败，未预约或已签到"
apiType: entity_related
controller: CheckInController
basePath: /api/checkins

---API_SEPARATOR---

// 第7个接口
basicInfo: 获取会员卡列表 GET /api/membership-cards - 查询会员卡信息
requestParams:
keyword: "可选 - 搜索关键词（卡号或姓名）"
responseParams:
// 注意：status_code 是 HTTP 状态码（元数据），不在 JSON 响应体中。
// 成功时直接返回 response_body 定义的实体结构。
success:
  status_code: 200
  response_body:
    - cardNumber: "YJ20241015001"
      memberName: "张三"
      status: "正常"
      statusUpdateTime: "2024-10-15 10:00:00"
    - cardNumber: "YJ20241015002"
      memberName: "李四"
      status: "冻结"
      statusUpdateTime: "2024-10-14 15:30:00"
error:
  status_code: 403
  response_body:
    error: "FORBIDDEN"
    message: "无权限访问"
apiType: entity_related
controller: MembershipCardController
basePath: /api/membership-cards

---API_SEPARATOR---

// 第8个接口
basicInfo: 更新会员卡状态 PUT /api/membership-cards/{cardNumber}/status - 变更会员卡状态
requestParams:
cardNumber: "YJ20241015001"
status: "正常"
responseParams:
// 注意：status_code 是 HTTP 状态码（元数据），不在 JSON 响应体中。
// 成功时直接返回 response_body 定义的实体结构。
success:
  status_code: 200
  response_body:
    cardNumber: "YJ20241015001"
    status: "正常"
    statusUpdateTime: "2024-10-15 18:00:00"
error:
  status_code: 400
  response_body:
    error: "STATUS_UPDATE_FAILED"
    message: "状态更新失败，无效的状态值"
apiType: entity_related
controller: MembershipCardController
basePath: /api/membership-cards

---API_SEPARATOR---

// 第9个接口
basicInfo: 取消预约 DELETE /api/appointments/{id} - 取消课程预约
requestParams:
id: 1
responseParams:
// 注意：status_code 是 HTTP 状态码（元数据），不在 JSON 响应体中。
// 成功时直接返回 response_body 定义的实体结构。
success:
  status_code: 200
  response_body:
    message: "预约取消成功"
error:
  status_code: 400
  response_body:
    error: "CANCEL_FAILED"
    message: "取消失败，已超过可取消时间"
apiType: entity_related
controller: AppointmentController
basePath: /api/appointments

---API_SEPARATOR---

// 第10个接口
basicInfo: 获取会员信息 GET /api/members/{id} - 查询会员详细信息
requestParams:
id: 1
responseParams:
// 注意：status_code 是 HTTP 状态码（元数据），不在 JSON 响应体中。
// 成功时直接返回 response_body 定义的实体结构。
success:
  status_code: 200
  response_body:
    id: 1
    phone: "13800001111"
    nickname: "张三"
    status: "正常"
    registerTime: "2024-01-15"
    cardNumber: "YJ20241015001"
    cardStatus: "正常"
error:
  status_code: 404
  response_body:
    error: "MEMBER_NOT_FOUND"
    message: "会员不存在"
apiType: entity_related
controller: MemberController
basePath: /api/members

---API_SEPARATOR---

---ENTITY_LIST_START---
@Table(name = "member_tbl")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "phone", nullable = false, length = 11, unique = true)
    private String phone;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "avatar", length = 255)
    private String avatar;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "register_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate registerTime;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt = LocalDateTime.now();
}

@Table(name = "course_tbl")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "coach", nullable = false, length = 50)
    private String coach;

    @Column(name = "start_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;

    @Column(name = "total_slots", nullable = false)
    private Integer totalSlots;

    @Column(name = "available_slots", nullable = false)
    private Integer availableSlots;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt = LocalDateTime.now();
}

@Table(name = "appointment_tbl")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "appointment_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime appointmentTime;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt = LocalDateTime.now();
}

@Table(name = "check_in_tbl")
public class CheckIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "check_in_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime checkInTime;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt = LocalDateTime.now();
}

@Table(name = "membership_card_tbl")
public class MembershipCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "card_number", nullable = false, length = 20, unique = true)
    private String cardNumber;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "status_update_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime statusUpdateTime = LocalDateTime.now();

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt = LocalDateTime.now();
}
---ENTITY_LIST_END---