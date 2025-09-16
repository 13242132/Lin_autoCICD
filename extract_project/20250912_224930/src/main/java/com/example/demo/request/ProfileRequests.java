package com.example.demo.request;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import lombok.Data;

public class ProfileRequests {

    @Data
    public static class GetProfileRequest {
        private Long userId;
    }
}
