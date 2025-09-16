package com.example.demo.service;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.api.service.SubscriptionApiService;
import com.example.demo.api.querydto.SubscriptionQueryDTO;

@Service
public class UserService {

    private final UserRepository repository;

    // 注入外部模块的Service
    @Autowired
    private SubscriptionApiService subscriptionApiService;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    // 调用数据库层：根据ID查询用户详情
    public Optional<User> getUserProfile(Long userId) {
        return repository.findById(userId);
    }

    // 获取用户订阅数（多表接口）
    public Long getSubscriptionCount(Long userId) {
        SubscriptionQueryDTO queryDTO = new SubscriptionQueryDTO();
        queryDTO.setUserId(userId);
        return (long) subscriptionApiService.queryByConditions(queryDTO).size();
    }
}
