package com.example.demo.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Subscription;
import com.example.demo.entity.User;
import com.example.demo.exception.BusinessException;
import com.example.demo.repository.SubscriptionRepository;
import com.example.demo.request.SubscriptionRequests;
import com.example.demo.response.SubscriptionResponses;
import com.example.demo.api.service.UserApiService;

@Service
public class SubscriptionService {

    private final SubscriptionRepository repository;
    private final UserApiService userApiService;

    public SubscriptionService(SubscriptionRepository repository, UserApiService userApiService) {
        this.repository = repository;
        this.userApiService = userApiService;
    }

    // 创建订阅
    public Subscription createSubscription(SubscriptionRequests.CreateSubscriptionRequest request) {
        Long userId = request.getUserId();
        String topicName = request.getTopicName();

        // 验证用户是否存在
        Optional<User> userOpt = userApiService.findById(userId);
        if (!userOpt.isPresent()) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在");
        }

        // 检查是否已订阅该主题
        Optional<Subscription> existingSubscription = repository.findByUserIdAndTopicName(userId, topicName);
        if (existingSubscription.isPresent()) {
            throw new BusinessException("SUBSCRIPTION_ALREADY_EXISTS", "已订阅该主题");
        }

        // 检查用户订阅数是否超过限制
        long subscriptionCount = repository.countByUserId(userId);
        if (subscriptionCount >= 5) {
            throw new BusinessException("SUBSCRIPTION_LIMIT_EXCEEDED", "最多可订阅5个主题");
        }

        // 创建新订阅
        Subscription subscription = new Subscription();
        subscription.setUserId(userId);
        subscription.setTopicName(topicName);
        subscription.setSubscribedAt(LocalDateTime.now());
        return repository.save(subscription);
    }

    // 获取用户订阅列表
    public List<SubscriptionResponses.SubscriptionItem> getSubscriptions(Long userId) {
        // 验证用户是否存在
        Optional<User> userOpt = userApiService.findById(userId);
        if (!userOpt.isPresent()) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在");
        }

        // 查询用户订阅
        List<Subscription> subscriptions = repository.findByUserId(userId);
        return subscriptions.stream()
                .map(sub -> {
                    SubscriptionResponses.SubscriptionItem item = new SubscriptionResponses.SubscriptionItem();
                    item.setTopicName(sub.getTopicName());
                    item.setSubscribedAt(sub.getSubscribedAt());
                    return item;
                })
                .collect(Collectors.toList());
    }

    // 删除订阅
    public void deleteSubscription(Long id) {
        Optional<Subscription> subscriptionOpt = repository.findById(id);
        if (!subscriptionOpt.isPresent()) {
            throw new BusinessException("SUBSCRIPTION_NOT_FOUND", "订阅记录不存在");
        }
        repository.deleteById(id);
    }
}
