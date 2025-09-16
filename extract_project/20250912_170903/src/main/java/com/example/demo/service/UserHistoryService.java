package com.example.demo.service;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import java.util.List;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.example.demo.entity.UserHistory;

import com.example.demo.entity.News;

import com.example.demo.repository.UserHistoryRepository;

import com.example.demo.api.querydto.NewsQueryDTO;

import com.example.demo.api.service.NewsApiService;

import com.example.demo.request.UserHistoryRequests;

import com.example.demo.response.UserHistoryResponses;

@Service
public class UserHistoryService {

    private final UserHistoryRepository repository;

    // 引入外部模块的Service
    @Autowired
    private NewsApiService newsApiService;

    public UserHistoryService(UserHistoryRepository repository) {
        this.repository = repository;
    }

    // 调用数据库层：更新用户阅读历史
    public UserHistory updateUserHistory(Long userId, UserHistoryRequests.UpdateUserHistoryRequest request) {
        // 先检查新闻是否存在
        Optional<News> newsOpt = newsApiService.findById(request.getNewsId());
        if (!newsOpt.isPresent()) {
            throw new RuntimeException("INVALID_NEWS_ID");
        }

        // 创建新的UserHistory对象
        UserHistory history = new UserHistory();
        history.setUserId(userId);
        history.setNewsId(request.getNewsId());
        history.setTitle(newsOpt.get().getTitle());
        history.setSource(newsOpt.get().getSource());
        history.setReadAt(java.time.LocalDateTime.parse(request.getReadAt()));

        return repository.insert(history);
    }

    // 调用数据库层：获取用户阅读历史
    public List<UserHistory> getUserHistory(Long userId, UserHistoryRequests.GetUserHistoryRequest request) {
        // 假设分页逻辑在Repository或Service中处理，这里简化只取所有记录
        return repository.findByUserId(userId);
    }
}
