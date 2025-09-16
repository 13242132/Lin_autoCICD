package com.example.demo.response;
import java.util.Optional;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import java.time.LocalDateTime;
import java.util.List;

public class NewsResponses {

    public static class NewsItem {
        private Long id;
        private String title;
        private String source;
        private LocalDateTime publishedAt;
        private String summary;

        public NewsItem(Long id, String title, String source, LocalDateTime publishedAt, String summary) {
            this.id = id;
            this.title = title;
            this.source = source;
            this.publishedAt = publishedAt;
            this.summary = summary;
        }

        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getSource() { return source; }
        public LocalDateTime getPublishedAt() { return publishedAt; }
        public String getSummary() { return summary; }
    }

    public static class GetNewsListResponse {
        private List<NewsItem> data;

        public GetNewsListResponse(List<NewsItem> data) {
            this.data = data;
        }

        public List<NewsItem> getData() { return data; }
    }

    public static class ErrorResponse {
        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() { return error; }
        public String getMessage() { return message; }
    }

}
