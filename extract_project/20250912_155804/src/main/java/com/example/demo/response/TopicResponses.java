package com.example.demo.response;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import java.util.List;

public class TopicResponses {

    public static class TopicItem {
        private Long id;
        private String name;

        public TopicItem(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
    }

    public static class GetAllTopicsResponse {
        private List<TopicItem> topics;

        public GetAllTopicsResponse(List<TopicItem> topics) {
            this.topics = topics;
        }

        public List<TopicItem> getTopics() { return topics; }
    }

    // 错误响应类
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
