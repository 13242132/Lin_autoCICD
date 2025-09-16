package com.example.demo.response;
import java.util.Optional;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import java.util.List;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

public class NewsResponses {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewsItem {
        private Long id;
        private String title;
        private String source;
        private LocalDateTime publishedAt;
        private String summary;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetNewsListResponse {
        private List<NewsItem> news;
        private long total;
        private int page;
        private int size;
        private int totalPages;
    }
}
