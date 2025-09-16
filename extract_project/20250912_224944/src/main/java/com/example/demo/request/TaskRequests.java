package com.example.demo.request;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import lombok.Data;

public class TaskRequests {

    @Data
    public static class CreateTaskRequest {
        private String title;
        private String description;
        private String priority;
        private LocalDateTime dueDate;
        private String assignee;
    }

    @Data
    public static class UpdateTaskStatusRequest {
        private String status;
    }

    @Data
    public static class GetTasksByStatusRequest {
        private String status;
    }

}
