package com.example.demo.request;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import lombok.Data;

public class SubscriptionRequests {

    @Data
    public static class CreateSubscriptionRequest {
        private Long userId;
        private String topicName;
    }

    @Data
    public static class GetSubscriptionsRequest {
        private Long userId;
    }
}
