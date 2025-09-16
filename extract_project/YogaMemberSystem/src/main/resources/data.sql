-- ========================================
-- Table: member_tbl
-- Description: 存储会员账户信息
-- Fields:
-- id BIGINT AUTO_INCREMENT PRIMARY KEY
-- phone VARCHAR(11) NOT NULL 唯一手机号
-- password VARCHAR(100) NOT NULL 密码（明文存储）
-- nickname VARCHAR(50) NOT NULL 昵称
-- avatar VARCHAR(255) NULL 头像URL
-- status VARCHAR(20) NOT NULL 账户状态
-- register_time DATE NOT NULL 注册日期
-- created_at TIMESTAMP NOT NULL 创建时间
-- ========================================
MERGE INTO member_tbl (phone, password, nickname, avatar, status, register_time, created_at)
KEY(phone)
VALUES ('13800001111', 'admin123', 'AdminUser', 'http://example.com/avatar1.jpg', 'ACTIVE', '2025-01-01', '2025-01-01 10:00:00');

MERGE INTO member_tbl (phone, password, nickname, avatar, status, register_time, created_at)
KEY(phone)
VALUES ('13900002222', 'user123', 'RegularUser1', 'http://example.com/avatar2.jpg', 'ACTIVE', '2025-01-02', '2025-01-02 11:00:00');

MERGE INTO member_tbl (phone, password, nickname, avatar, status, register_time, created_at)
KEY(phone)
VALUES ('13700003333', 'user456', 'RegularUser2', 'http://example.com/avatar3.jpg', 'INACTIVE', '2025-01-03', '2025-01-03 12:00:00');

-- ========================================
-- Table: course_tbl
-- Description: 存储课程信息
-- Fields:
-- id BIGINT AUTO_INCREMENT PRIMARY KEY
-- name VARCHAR(100) NOT NULL 课程名称
-- coach VARCHAR(50) NOT NULL 教练姓名
-- start_time TIMESTAMP NOT NULL 开始时间
-- end_time TIMESTAMP NOT NULL 结束时间
-- total_slots INTEGER NOT NULL 总座位数
-- available_slots INTEGER NOT NULL 可用座位数
-- status VARCHAR(20) NOT NULL 课程状态
-- created_at TIMESTAMP NOT NULL 创建时间
-- ========================================
MERGE INTO course_tbl (name, coach, start_time, end_time, total_slots, available_slots, status, created_at)
KEY(name, coach)
VALUES ('瑜伽基础班', '李教练', '2025-01-10 09:00:00', '2025-01-10 10:30:00', 15, 10, 'SCHEDULED', '2025-01-01 10:00:00');

MERGE INTO course_tbl (name, coach, start_time, end_time, total_slots, available_slots, status, created_at)
KEY(name, coach)
VALUES ('动感单车', '王教练', '2025-01-11 15:00:00', '2025-01-11 16:30:00', 20, 5, 'SCHEDULED', '2025-01-02 11:00:00');

MERGE INTO course_tbl (name, coach, start_time, end_time, total_slots, available_slots, status, created_at)
KEY(name, coach)
VALUES ('普拉提进阶', '张教练', '2025-01-12 18:00:00', '2025-01-12 19:30:00', 10, 0, 'COMPLETED', '2025-01-03 12:00:00');

-- ========================================
-- Table: appointment_tbl
-- Description: 存储预约记录
-- Fields:
-- id BIGINT AUTO_INCREMENT PRIMARY KEY
-- course_id BIGINT NOT NULL 课程ID
-- member_id BIGINT NOT NULL 会员ID
-- appointment_time TIMESTAMP NOT NULL 预约时间
-- status VARCHAR(20) NOT NULL 预约状态
-- created_at TIMESTAMP NOT NULL 创建时间
-- ========================================
MERGE INTO appointment_tbl (course_id, member_id, appointment_time, status, created_at)
KEY(course_id, member_id)
VALUES (1, 1, '2025-01-05 14:30:00', 'CONFIRMED', '2025-01-05 14:30:00');

MERGE INTO appointment_tbl (course_id, member_id, appointment_time, status, created_at)
KEY(course_id, member_id)
VALUES (2, 2, '2025-01-06 10:15:00', 'PENDING', '2025-01-06 10:15:00');

MERGE INTO appointment_tbl (course_id, member_id, appointment_time, status, created_at)
KEY(course_id, member_id)
VALUES (1, 3, '2025-01-07 09:45:00', 'CANCELLED', '2025-01-07 09:45:00');

-- ========================================
-- Table: check_in_tbl
-- Description: 存储签到记录
-- Fields:
-- id BIGINT AUTO_INCREMENT PRIMARY KEY
-- course_id BIGINT NOT NULL 课程ID
-- member_id BIGINT NOT NULL 会员ID
-- check_in_time TIMESTAMP NOT NULL 签到时间
-- status VARCHAR(20) NOT NULL 签到状态
-- created_at TIMESTAMP NOT NULL 创建时间
-- ========================================
MERGE INTO check_in_tbl (course_id, member_id, check_in_time, status, created_at)
KEY(course_id, member_id)
VALUES (1, 1, '2025-01-10 08:55:00', 'CHECKED_IN', '2025-01-10 08:55:00');

MERGE INTO check_in_tbl (course_id, member_id, check_in_time, status, created_at)
KEY(course_id, member_id)
VALUES (2, 2, '2025-01-11 14:50:00', 'ABSENT', '2025-01-11 16:40:00');

-- ========================================
-- Table: membership_card_tbl
-- Description: 存储会员卡信息
-- Fields:
-- id BIGINT AUTO_INCREMENT PRIMARY KEY
-- card_number VARCHAR(20) NOT NULL 唯一卡号
-- member_id BIGINT NOT NULL 会员ID
-- status VARCHAR(20) NOT NULL 卡状态
-- status_update_time TIMESTAMP NOT NULL 状态更新时间
-- created_at TIMESTAMP NOT NULL 创建时间
-- ========================================
MERGE INTO membership_card_tbl (card_number, member_id, status, status_update_time, created_at)
KEY(card_number)
VALUES ('CARD20250001', 1, 'ACTIVE', '2025-01-01 10:00:00', '2025-01-01 10:00:00');

MERGE INTO membership_card_tbl (card_number, member_id, status, status_update_time, created_at)
KEY(card_number)
VALUES ('CARD20250002', 2, 'EXPIRED', '2025-01-02 11:00:00', '2025-01-02 11:00:00');

MERGE INTO membership_card_tbl (card_number, member_id, status, status_update_time, created_at)
KEY(card_number)
VALUES ('CARD20250003', 3, 'SUSPENDED', '2025-01-03 12:00:00', '2025-01-03 12:00:00');