package com.example.demo.controller;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.News;
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
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) String source,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        NewsRequests.GetNewsListRequest request = new NewsRequests.GetNewsListRequest(category, source, page, size);
        NewsResponses.GetNewsListResponse response = service.getNewsList(request);
        return ResponseEntity.ok(response);
    }

    // 创建新闻
    @PostMapping
    public ResponseEntity<NewsResponses.CreateNewsResponse> createNews(@RequestBody News news) {
        News created = service.createNews(news);
        NewsResponses.CreateNewsResponse response = new NewsResponses.CreateNewsResponse();
        response.setId(created.getId());
        response.setTitle(created.getTitle());
        response.setSource(created.getSource());
        response.setPublishedAt(created.getPublishedAt());
        response.setSummary(created.getSummary());
        response.setUrl(created.getUrl());
        response.setImageUrl(created.getImageUrl());
        response.setCategoryId(created.getCategoryId());
        response.setCreatedAt(created.getCreatedAt());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 获取新闻详情
    @GetMapping("/{id}")
    public ResponseEntity<NewsResponses.GetNewsDetailResponse> getNewsDetail(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-ID", required = false) Long currentUserId) {

        NewsResponses.GetNewsDetailResponse response = service.getNewsDetail(id, currentUserId);
        return ResponseEntity.ok(response);
    }
}
