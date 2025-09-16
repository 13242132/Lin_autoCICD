package com.example.demo.auth.request;


import lombok.Data;

public class AuthRequests {

    @Data
    public static class RegisterRequest {
        private String username;
        private String email;
        private String password;
    }

    @Data
    public static class LoginRequest {
        private String usernameOrEmail;
        private String password;
    }
}
