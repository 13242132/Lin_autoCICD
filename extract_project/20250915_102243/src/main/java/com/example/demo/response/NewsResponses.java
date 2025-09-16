package com.example.demo.response;
import java.util.Optional;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

public class NewsResponses {

    @Data
    public static class GetNewsListResponse {
        private List<NewsItem> news;
        private long total;
        private int page;
        private int size;
        private int totalPages;
    }

    @Data
    public static class NewsItem {
        private Long id;
        private String title;
        private String source;
        private LocalDateTime publishedAt;
        private String summary;
    }

    @Data
    public static class ErrorResponse {
        private String error;
        private String message;
    }
}
