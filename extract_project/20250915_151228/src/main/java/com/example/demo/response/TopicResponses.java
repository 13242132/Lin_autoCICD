package com.example.demo.response;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

public class TopicResponses {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopicItem {
        private Long id;
        private String name;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetTopicsResponse {
        private List<TopicItem> topics;
    }

}
