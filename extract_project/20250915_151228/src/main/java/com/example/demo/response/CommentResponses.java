package com.example.demo.response;
import java.util.Optional;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

public class CommentResponses {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCommentResponse {
        private Long id;
        private Long newsId;
        private Long userId;
        private String content;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetCommentByIdResponse {
        private Long id;
        private Long newsId;
        private Long userId;
        private String content;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentItem {
        private Long id;
        private Long newsId;
        private Long userId;
        private String content;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetCommentsByNewsIdResponse {
        private List<CommentItem> comments;
        private long total;
        private int page;
        private int size;
        private int totalPages;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetUserCommentOnNewsResponse {
        private Long id;
        private Long newsId;
        private Long userId;
        private String content;
        private LocalDateTime createdAt;
    }

}
