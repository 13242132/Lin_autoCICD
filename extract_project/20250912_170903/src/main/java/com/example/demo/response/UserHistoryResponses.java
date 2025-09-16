package com.example.demo.response;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import java.util.List;

public class UserHistoryResponses {

    public static class UpdateUserHistoryResponse {
        private Long id;
        private Long userId;
        private Long newsId;
        private String title;
        private String source;
        private String readAt;

        public UpdateUserHistoryResponse(Long id, Long userId, Long newsId, String title, String source, String readAt) {
            this.id = id;
            this.userId = userId;
            this.newsId = newsId;
            this.title = title;
            this.source = source;
            this.readAt = readAt;
        }

        public Long getId() { return id; }
        public Long getUserId() { return userId; }
        public Long getNewsId() { return newsId; }
        public String getTitle() { return title; }
        public String getSource() { return source; }
        public String getReadAt() { return readAt; }
    }

    public static class GetUserHistoryResponse {
        private List<UserHistoryItem> items;
        private Long total;
        private Integer page;
        private Integer size;

        public GetUserHistoryResponse(List<UserHistoryItem> items, Long total, Integer page, Integer size) {
            this.items = items;
            this.total = total;
            this.page = page;
            this.size = size;
        }

        public List<UserHistoryItem> getItems() { return items; }
        public Long getTotal() { return total; }
        public Integer getPage() { return page; }
        public Integer getSize() { return size; }

        public static class UserHistoryItem {
            private Long id;
            private Long userId;
            private Long newsId;
            private String title;
            private String source;
            private String readAt;

            public UserHistoryItem(Long id, Long userId, Long newsId, String title, String source, String readAt) {
                this.id = id;
                this.userId = userId;
                this.newsId = newsId;
                this.title = title;
                this.source = source;
                this.readAt = readAt;
            }

            public Long getId() { return id; }
            public Long getUserId() { return userId; }
            public Long getNewsId() { return newsId; }
            public String getTitle() { return title; }
            public String getSource() { return source; }
            public String getReadAt() { return readAt; }
        }
    }
}
