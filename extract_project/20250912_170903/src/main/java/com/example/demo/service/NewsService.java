package com.example.demo.service;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import java.util.List;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

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

    // 调用数据库层：查询新闻列表
    public NewsResponses.GetNewsListResponse getNewsList(NewsRequests.GetNewsListRequest request) {
        int page = request.getPage();
        int size = request.getSize();
        String category = request.getCategory();

        // 分页计算
        int offset = (page - 1) * size;

        List<News> newsList;
        long total;

        if (category == null || category.isEmpty()) {
            newsList = repository.findAllOrderedByPublishedAt(offset, size);
            total = repository.countAllNews();
        } else {
            newsList = repository.findByCategoryAndPage(category, offset, size);
            total = repository.countNewsByCategory(category);
        }

        List<NewsResponses.GetNewsListResponse.NewsItem> items = newsList.stream()
                .map(news -> new NewsResponses.GetNewsListResponse.NewsItem(
                        news.getId(),
                        news.getTitle(),
                        news.getSource(),
                        news.getPublishedAt().toString(),
                        news.getSummary()
                ))
                .collect(Collectors.toList());

        return new NewsResponses.GetNewsListResponse(items, total, page, size);
    }
}
