package com.example.demo.response;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import java.util.List;
import lombok.Data;

public class NewsResponses {
    
    @Data
    public static class GetNewsListResponse {
        private List<NewsItem> content;
        private long totalElements;
        private int totalPages;
        private int number;
        
        @Data
        public static class NewsItem {
            private Long id;
            private String title;
            private String source;
            private String publishedAt;
            private String summary;
        }
    }
}
