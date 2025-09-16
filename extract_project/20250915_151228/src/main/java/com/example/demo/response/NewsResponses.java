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
    public static class GetNewsListResponse {
        private List<NewsItem> news;
        private long total;
        private int page;
        private int size;
        private int totalPages;
    }

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
    public static class GetNewsDetailResponse {
        private Long id;
        private String title;
        private String source;
        private LocalDateTime publishedAt;
        private String summary;
        private String url;
        private String imageUrl;
        private List<CommentItem> comments;
        private long likesCount;
        private boolean isLiked;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentItem {
        private Long id;
        private Long userId;
        private String content;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateNewsResponse {
        private Long id;
        private String title;
        private String source;
        private LocalDateTime publishedAt;
        private String summary;
        private String url;
        private String imageUrl;
        private Integer categoryId;
        private LocalDateTime createdAt;
    }
}
