package com.example.demo.request;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import java.util.List;

public class SubscriptionRequests {
    
    public static class UpdateSubscriptionsRequest {
        private List<String> topicNames;
        
        public UpdateSubscriptionsRequest(List<String> topicNames) {
            this.topicNames = topicNames;
        }
        
        public List<String> getTopicNames() {
            return topicNames;
        }
    }
}
