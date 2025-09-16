package com.example.demo.request;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


public class UserHistoryRequests {

    public static class UpdateUserHistoryRequest {
        private Long newsId;
        private String readAt;

        public UpdateUserHistoryRequest(Long newsId, String readAt) {
            this.newsId = newsId;
            this.readAt = readAt;
        }

        public Long getNewsId() { return newsId; }
        public String getReadAt() { return readAt; }
    }

    public static class GetUserHistoryRequest {
        private Integer page;
        private Integer size;

        public GetUserHistoryRequest(Integer page, Integer size) {
            this.page = page;
            this.size = size;
        }

        public Integer getPage() { return page; }
        public Integer getSize() { return size; }
    }
}
