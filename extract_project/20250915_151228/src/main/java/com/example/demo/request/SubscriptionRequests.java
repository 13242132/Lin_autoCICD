package com.example.demo.request;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

public class SubscriptionRequests {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateSubscriptionRequest {
        private Long userId;
        private String topicName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetSubscriptionsRequest {
        private Long userId;
    }
}
