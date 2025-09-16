package com.example.demo.auth.response;


import lombok.Data;
import java.time.LocalDateTime;

public class AuthResponses {

    @Data
    public static class RegisterResponse {
        private Long id;
        private String username;
        private String email;
        private LocalDateTime createdAt;

        public RegisterResponse(Long id, String username, String email, LocalDateTime createdAt) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.createdAt = createdAt;
        }
    }

    @Data
    public static class LoginResponse {
        private String token;
        private UserInfo user;

        public LoginResponse(String token, UserInfo user) {
            this.token = token;
            this.user = user;
        }
    }

    @Data
    public static class UserInfo {
        private Long userId;
        private String username;
        private String email;

        public UserInfo(Long userId, String username, String email) {
            this.userId = userId;
            this.username = username;
            this.email = email;
        }
    }
}
