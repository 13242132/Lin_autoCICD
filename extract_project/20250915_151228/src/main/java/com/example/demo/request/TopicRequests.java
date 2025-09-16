package com.example.demo.request;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

public class TopicRequests {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetTopicsRequest {
        // 无参数
    }

}
