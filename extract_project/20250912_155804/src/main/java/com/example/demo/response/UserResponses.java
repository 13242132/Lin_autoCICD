package com.example.demo.response;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


public class UserResponses {
    
    public static class GetProfileResponse {
        private Long id;
        private String username;
        private String email;
        private String createdAt;
        private Long subscriptionCount;
        private String lastLoginAt;
        
        public GetProfileResponse(Long id, String username, String email, String createdAt, Long subscriptionCount, String lastLoginAt) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.createdAt = createdAt;
            this.subscriptionCount = subscriptionCount;
            this.lastLoginAt = lastLoginAt;
        }
        
        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getCreatedAt() { return createdAt; }
        public Long getSubscriptionCount() { return subscriptionCount; }
        public String getLastLoginAt() { return lastLoginAt; }
    }
}
