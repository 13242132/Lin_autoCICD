package com.example.demo.service;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Subscription;
import com.example.demo.entity.User;
import com.example.demo.repository.SubscriptionRepository;
import com.example.demo.api.service.UserApiService;
import com.example.demo.request.SubscriptionRequests;

@Service
public class SubscriptionService {

    private final SubscriptionRepository repository;
    private final UserApiService userApiService;

    public SubscriptionService(SubscriptionRepository repository, UserApiService userApiService) {
        this.repository = repository;
        this.userApiService = userApiService;
    }

    // 创建订阅
    public Subscription createSubscription(Long userId, SubscriptionRequests.CreateSubscriptionRequest request) {
        // 检查用户是否存在
        Optional<User> userOpt = userApiService.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("USER_NOT_FOUND");
        }

        // 检查是否超过订阅上限
        long count = repository.countByUserId(userId);
        if (count >= 5) {
            throw new RuntimeException("SUBSCRIPTION_LIMIT_EXCEEDED");
        }

        // 构建订阅对象
        Subscription subscription = new Subscription();
        subscription.setUserId(userId);
        subscription.setTopicName(request.getTopicName());
        subscription.setSubscribedAt(LocalDateTime.now());

        // 保存订阅
        return repository.insert(subscription);
    }

    // 获取用户订阅列表
    public List<Subscription> getSubscriptions(Long userId) {
        // 检查用户是否存在
        Optional<User> userOpt = userApiService.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("USER_NOT_FOUND");
        }

        return repository.findByUserId(userId);
    }

    // 删除订阅
    public void deleteSubscription(Long userId, String topicName) {
        // 检查用户是否存在
        Optional<User> userOpt = userApiService.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("USER_NOT_FOUND");
        }

        // 查找订阅记录
        Optional<Subscription> subscriptionOpt = repository.findByUserIdAndTopicName(userId, topicName);
        if (!subscriptionOpt.isPresent()) {
            throw new RuntimeException("SUBSCRIPTION_NOT_FOUND");
        }

        // 删除订阅记录
        repository.deleteById(subscriptionOpt.get().getId());
    }
}
