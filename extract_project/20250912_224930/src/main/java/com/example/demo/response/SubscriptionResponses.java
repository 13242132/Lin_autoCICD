package com.example.demo.response;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import java.util.List;
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
    public static class GetSubscriptionsResponse {
        private Long userId;
        private List<TopicItem> topics;

        @Data
        public static class TopicItem {
            private String topicName;
            private String subscribedAt;
        }
    }

    @Data
    public static class ErrorResponse {
        private String error;
        private String message;
    }
}
