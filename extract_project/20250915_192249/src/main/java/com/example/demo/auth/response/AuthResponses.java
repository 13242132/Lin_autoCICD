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
        private Long userId;
        private String username;

        public LoginResponse(String token, Long userId, String username) {
            this.token = token;
            this.userId = userId;
            this.username = username;
        }
    }
}
