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

import com.example.demo.repository.SubscriptionRepository;
import com.example.demo.api.service.UserApiService;
import com.example.demo.request.SubscriptionRequests;
import com.example.demo.response.SubscriptionResponses;

@Service
public class SubscriptionService {

    private final SubscriptionRepository repository;
    private final UserApiService userApiService;

    public SubscriptionService(SubscriptionRepository repository, UserApiService userApiService) {
        this.repository = repository;
        this.userApiService = userApiService;
    }

    // 创建用户主题订阅
    public SubscriptionResponses.CreateSubscriptionResponse createSubscription(SubscriptionRequests.CreateSubscriptionRequest request) {
        Long userId = request.getUserId();
        String topicName = request.getTopicName();

        // 检查用户是否存在
        Optional<User> userOpt = userApiService.findById(userId);
  

        // 检查是否已订阅该主题
        Optional<Subscription> existingSubscription = repository.findByUserIdAndTopicName(userId, topicName);
   

        // 检查用户订阅数是否超过限制（最多5个）
        long subscriptionCount = repository.countByUserId(userId);
   

        // 创建新订阅
        Subscription subscription = new Subscription();
        subscription.setUserId(userId);
        subscription.setTopicName(topicName);
        subscription.setSubscribedAt(LocalDateTime.now());

        Subscription saved = repository.save(subscription);

        // 构造响应
        SubscriptionResponses.CreateSubscriptionResponse response = new SubscriptionResponses.CreateSubscriptionResponse();
        response.setId(saved.getId());
        response.setUserId(saved.getUserId());
        response.setTopicName(saved.getTopicName());
        response.setSubscribedAt(saved.getSubscribedAt().toString());
        return response;
    }

    // 获取用户订阅主题列表
    public List<SubscriptionResponses.SubscriptionItem> getSubscriptions(SubscriptionRequests.GetSubscriptionsRequest request) {
        Long userId = request.getUserId();

        // 检查用户是否存在
        Optional<User> userOpt = userApiService.findById(userId);
 

        // 查询用户订阅记录
        List<Subscription> subscriptions = repository.findByUserId(userId);

        // 转换为响应对象
        return subscriptions.stream()
                .map(sub -> {
                    SubscriptionResponses.SubscriptionItem item = new SubscriptionResponses.SubscriptionItem();
                    item.setTopicName(sub.getTopicName());
                    item.setSubscribedAt(sub.getSubscribedAt().toString());
                    return item;
                })
                .collect(Collectors.toList());
    }

    // 删除用户主题订阅
    public void deleteSubscription(Long id) {
        Optional<Subscription> subscriptionOpt = repository.findById(id);


        repository.deleteById(id);
    }
}
