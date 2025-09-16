package com.example.demo.response;
import java.util.Optional;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import java.time.LocalDateTime;
import java.util.List;

public class SubscriptionResponses {

    public static class GetSubscriptionsResponse {
        private Long userId;
        private List<TopicItem> topics;
        private Integer total;

        public GetSubscriptionsResponse(Long userId, List<TopicItem> topics, Integer total) {
            this.userId = userId;
            this.topics = topics;
            this.total = total;
        }

        public Long getUserId() {
            return userId;
        }

        public List<TopicItem> getTopics() {
            return topics;
        }

        public Integer getTotal() {
            return total;
        }

        public static class TopicItem {
            private String topicName;
            private String subscribedAt;

            public TopicItem(String topicName, String subscribedAt) {
                this.topicName = topicName;
                this.subscribedAt = subscribedAt;
            }

            public String getTopicName() {
                return topicName;
            }

            public String getSubscribedAt() {
                return subscribedAt;
            }
        }
    }

    public static class CreateSubscriptionResponse {
        private Long id;
        private Long userId;
        private String topicName;
        private String subscribedAt;

        public CreateSubscriptionResponse(Long id, Long userId, String topicName, String subscribedAt) {
            this.id = id;
            this.userId = userId;
            this.topicName = topicName;
            this.subscribedAt = subscribedAt;
        }

        public Long getId() {
            return id;
        }

        public Long getUserId() {
            return userId;
        }

        public String getTopicName() {
            return topicName;
        }

        public String getSubscribedAt() {
            return subscribedAt;
        }
    }

    public static class ErrorResponse {
        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }
    }
}
