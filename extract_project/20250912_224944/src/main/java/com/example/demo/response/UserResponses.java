package com.example.demo.response;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import java.util.List;

import lombok.Data;

public class UserResponses {

    @Data
    public static class GetUsersResponse {
        private List<UserItem> users;
        private long total;
        private int page;
        private int size;
        private int totalPages;

        @Data
        public static class UserItem {
            private Long id;
            private String username;
            private String role;
            private String createdAt;
        }
    }

    @Data
    public static class DeleteUserResponse {
        private String error;
        private String message;
    }
}
