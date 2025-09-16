package com.example.demo.response;
import java.util.Optional;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

public class TaskResponses {

    @Data
    public static class CreateTaskResponse {
        private Long id;
        private String title;
        private String description;
        private String priority;
        private LocalDateTime dueDate;
        private String assignee;
        private String status;
        private LocalDateTime createdAt;
    }

    @Data
    public static class TaskItem {
        private Long id;
        private String title;
        private String priority;
        private LocalDateTime dueDate;
        private String assignee;
        private String status;
        private LocalDateTime createdAt;
    }

    @Data
    public static class GetTasksResponse {
        private List<TaskItem> items;
        private long total;
        private int page;
        private int size;
        private int totalPages;
    }

    @Data
    public static class UpdateTaskStatusResponse {
        private Long id;
        private String title;
        private String status;
        private LocalDateTime updatedAt;
        private List<HistoryItem> history;

        @Data
        public static class HistoryItem {
            private String timestamp;
            private String action;
            private String from;
            private String to;
        }
    }

    @Data
    public static class GetTaskHistoryResponse {
        private List<HistoryItem> history;

        @Data
        public static class HistoryItem {
            private String timestamp;
            private String action;
            private String from;
            private String to;
        }
    }

    @Data
    public static class ErrorResponse {
        private String error;
        private String message;
    }
}
