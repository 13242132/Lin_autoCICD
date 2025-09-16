package com.example.demo.response;
import java.util.List;
import java.util.Optional;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import java.time.LocalDateTime;
import lombok.Data;

public class SubscriptionResponses {

    @Data
    public static class CreateSubscriptionResponse {
        private Long id;
        private Long userId;
        private String topicName;
        private String subscribedAt;
    }

    @Data
    public static class SubscriptionItem {
        private String topicName;
        private String subscribedAt;
    }

    @Data
    public static class ErrorResponse {
        private String error;
        private String message;
    }
}
