package com.example.demo.response;
import java.util.List;
import java.util.Optional;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

public class SubscriptionResponses {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubscriptionItem {
        private String topicName;
        private LocalDateTime subscribedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateSubscriptionResponse {
        private Long id;
        private Long userId;
        private String topicName;
        private LocalDateTime subscribedAt;
    }
}
