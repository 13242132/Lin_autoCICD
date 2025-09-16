package com.example.demo.response;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import java.util.List;

import lombok.Data;

public class ProfileResponses {

    @Data
    public static class GetProfileResponse {
        private Long id;
        private String username;
        private String email;
        private String avatar;
        private String status;
        private String createdAt;
        private List<String> subscriptions;
    }

    // 错误响应类
    @Data
    public static class ErrorResponse {
        private String error;
        private String message;
    }
}
