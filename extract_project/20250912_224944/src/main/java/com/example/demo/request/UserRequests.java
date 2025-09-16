package com.example.demo.request;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import lombok.Data;

public class UserRequests {

    @Data
    public static class GetUsersRequest {
        private String role;
    }

    @Data
    public static class DeleteUserRequest {
        private Long userId;
    }
}
