package com.example.demo.request;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import lombok.Data;

public class NewsRequests {
    
    @Data
    public static class GetNewsListRequest {
        private String category;
        private Integer page = 1;
        private Integer size = 10;
    }
}
