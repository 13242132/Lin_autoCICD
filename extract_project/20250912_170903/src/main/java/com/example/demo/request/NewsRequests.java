package com.example.demo.request;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;


public class NewsRequests {

    public static class GetNewsListRequest {
        private String category;
        private Integer page;
        private Integer size;

        public GetNewsListRequest(String category, Integer page, Integer size) {
            this.category = category;
            this.page = page != null ? page : 1;
            this.size = size != null ? size : 10;
        }

        public String getCategory() { return category; }
        public Integer getPage() { return page; }
        public Integer getSize() { return size; }
    }
}
