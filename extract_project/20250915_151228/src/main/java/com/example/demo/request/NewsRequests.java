package com.example.demo.request;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

public class NewsRequests {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetNewsListRequest {
        private Integer category;
        private String source;
        private Integer page = 1;
        private Integer size = 10;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateNewsRequest {
        private String title;
        private String source;
        private String publishedAt;
        private String summary;
        private String url;
        private String imageUrl;
        private Integer categoryId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetNewsDetailRequest {
        private Long id;
    }
}
