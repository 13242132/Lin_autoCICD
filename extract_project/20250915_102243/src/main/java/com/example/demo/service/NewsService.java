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

@Service
public class NewsService {

    private final NewsRepository repository;

    public NewsService(NewsRepository repository) {
        this.repository = repository;
    }

    // 调用数据库层：获取新闻列表
    public NewsResponses.GetNewsListResponse getNewsList(NewsRequests.GetNewsListRequest request) {
        // 处理分页参数
        int page = Math.max(request.getPage() - 1, 0); // 转换为从0开始的页码
        int size = request.getSize() > 0 ? request.getSize() : 10;

        Pageable pageable = PageRequest.of(page, size);

        // 查询新闻列表
        Page<News> newsPage = repository.findByCategory(request.getCategory(), pageable);

        // 构造响应
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
        response.setSize(size);
        response.setTotalPages(newsPage.getTotalPages());

        return response;
    }
}
