package com.example.demo.response;
import java.util.Optional;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import java.time.LocalDateTime;
import java.util.List;

public class SubscriptionResponses {
    
    public static class SubscriptionItem {
        private Long id;
        private String topicName;
        private LocalDateTime subscribedAt;
        
        public SubscriptionItem(Long id, String topicName, LocalDateTime subscribedAt) {
            this.id = id;
            this.topicName = topicName;
            this.subscribedAt = subscribedAt;
        }
        
        public Long getId() {
            return id;
        }
        
        public String getTopicName() {
            return topicName;
        }
        
        public LocalDateTime getSubscribedAt() {
            return subscribedAt;
        }
    }
    
    public static class UpdateSubscriptionsResponse {
        private String message;
        private int updatedCount;
        
        public UpdateSubscriptionsResponse(String message, int updatedCount) {
            this.message = message;
            this.updatedCount = updatedCount;
        }
        
        public String getMessage() {
            return message;
        }
        
        public int getUpdatedCount() {
            return updatedCount;
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
