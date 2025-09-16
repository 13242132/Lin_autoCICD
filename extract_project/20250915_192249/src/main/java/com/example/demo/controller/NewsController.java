package com.example.demo.controller;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.service.NewsService;
import com.example.demo.request.NewsRequests;
import com.example.demo.response.NewsResponses;
import com.example.demo.exception.BusinessException;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService service;

    public NewsController(NewsService service) {
        this.service = service;
    }

    // 获取新闻列表
    @GetMapping
    public ResponseEntity<NewsResponses.GetNewsListResponse> getNewsList(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        NewsRequests.GetNewsListRequest request = new NewsRequests.GetNewsListRequest(category, page, size);
        NewsResponses.GetNewsListResponse response = service.getNewsList(request);
        return ResponseEntity.ok(response);
    }
}
