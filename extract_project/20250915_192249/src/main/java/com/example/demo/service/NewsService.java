package com.example.demo.service;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.entity.News;
import com.example.demo.repository.NewsRepository;
import com.example.demo.request.NewsRequests;
import com.example.demo.response.NewsResponses;
import com.example.demo.exception.BusinessException;

@Service
public class NewsService {

    private final NewsRepository repository;

    public NewsService(NewsRepository repository) {
        this.repository = repository;
    }

    // 调用数据库层：获取新闻列表
    public NewsResponses.GetNewsListResponse getNewsList(NewsRequests.GetNewsListRequest request) {
        // 构造分页请求
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());

        // 根据分类筛选新闻
        Page<News> newsPage = repository.findByCategory(request.getCategory(), pageable);

        // 构建响应
        NewsResponses.GetNewsListResponse response = new NewsResponses.GetNewsListResponse();
        response.setNews(newsPage.getContent().stream()
                .map(news -> {
                    NewsResponses.NewsItem item = new NewsResponses.NewsItem();
                    item.setId(news.getId());
                    item.setTitle(news.getTitle());
                    item.setSource(news.getSource());
                    item.setPublishedAt(news.getPublishedAt());
                    item.setSummary(news.getSummary());
                    return item;
                })
                .collect(Collectors.toList()));
        response.setTotal(newsPage.getTotalElements());
        response.setPage(request.getPage());
        response.setSize(request.getSize());
        response.setTotalPages(newsPage.getTotalPages());

        return response;
    }

    // 根据ID获取单条新闻
    public News getNewsById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException("NEWS_NOT_FOUND", "新闻不存在"));
    }

    // 获取所有新闻（无分页）
    public List<News> getAllNews() {
        return repository.findAll(Pageable.unpaged()).getContent();
    }
}
