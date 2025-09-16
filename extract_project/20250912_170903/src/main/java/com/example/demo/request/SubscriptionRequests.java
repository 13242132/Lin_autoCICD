package com.example.demo.request;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


public class SubscriptionRequests {

    public static class CreateSubscriptionRequest {
        private String topicName;

        public CreateSubscriptionRequest(String topicName) {
            this.topicName = topicName;
        }

        public String getTopicName() {
            return topicName;
        }
    }
}
