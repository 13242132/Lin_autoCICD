package com.example.demo.response;
import java.util.List;
import java.util.Optional;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import java.time.LocalDateTime;

public class UserResponses {

    public static class UserProfileResponse {
        private Long id;
        private String username;
        private String email;
        private String avatar;
        private String status;
        private LocalDateTime createdAt;
        private Long subscriptionCount;
        private LocalDateTime lastLoginAt;

        public UserProfileResponse(Long id, String username, String email, String avatar, String status,
                                   LocalDateTime createdAt, Long subscriptionCount, LocalDateTime lastLoginAt) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.avatar = avatar;
            this.status = status;
            this.createdAt = createdAt;
            this.subscriptionCount = subscriptionCount;
            this.lastLoginAt = lastLoginAt;
        }

        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getAvatar() { return avatar; }
        public String getStatus() { return status; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public Long getSubscriptionCount() { return subscriptionCount; }
        public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    }

    public static class ErrorResponse {
        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() { return error; }
        public String getMessage() { return message; }
    }
}
