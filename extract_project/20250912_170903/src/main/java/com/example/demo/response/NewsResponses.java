package com.example.demo.response;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import java.util.List;

public class NewsResponses {

    public static class GetNewsListResponse {
        private List<NewsItem> items;
        private long total;
        private int page;
        private int size;

        public GetNewsListResponse(List<NewsItem> items, long total, int page, int size) {
            this.items = items;
            this.total = total;
            this.page = page;
            this.size = size;
        }

        public List<NewsItem> getItems() { return items; }
        public long getTotal() { return total; }
        public int getPage() { return page; }
        public int getSize() { return size; }

        public static class NewsItem {
            private Long id;
            private String title;
            private String source;
            private String publishedAt;
            private String summary;

            public NewsItem(Long id, String title, String source, String publishedAt, String summary) {
                this.id = id;
                this.title = title;
                this.source = source;
                this.publishedAt = publishedAt;
                this.summary = summary;
            }

            public Long getId() { return id; }
            public String getTitle() { return title; }
            public String getSource() { return source; }
            public String getPublishedAt() { return publishedAt; }
            public String getSummary() { return summary; }
        }
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
