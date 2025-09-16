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
import com.example.demo.repository.UserRepository;
import com.example.demo.request.SubscriptionRequests;
import com.example.demo.response.SubscriptionResponses;

@Service
public class SubscriptionService {
    
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    
    public SubscriptionService(SubscriptionRepository subscriptionRepository, UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }
    
    // 获取用户订阅列表
    public List<SubscriptionResponses.SubscriptionItem> getSubscriptionsByUserId(Long userId) {
        // 验证用户是否存在
        if (!userRepository.existsById(userId)) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在");
        }
        
        List<Subscription> subscriptions = subscriptionRepository.findByUserId(userId);
        
        return subscriptions.stream()
                .map(sub -> {
                    SubscriptionResponses.SubscriptionItem item = new SubscriptionResponses.SubscriptionItem();
                    item.setId(sub.getId());
                    item.setTopicName(sub.getTopicName());
                    item.setSubscribedAt(sub.getSubscribedAt());
                    return item;
                })
                .collect(Collectors.toList());
    }
    
    // 创建用户订阅关系
    public SubscriptionResponses.CreateSubscriptionResponse createSubscription(Long userId, SubscriptionRequests.CreateSubscriptionRequest request) {
        // 验证用户是否存在
        if (!userRepository.existsById(userId)) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在");
        }
        
        // 检查是否已订阅该主题
        Optional<Subscription> existingSubscription = subscriptionRepository.findByUserIdAndTopicName(userId, request.getTopicName());
        if (existingSubscription.isPresent()) {
            throw new BusinessException("SUBSCRIPTION_EXISTS", "该主题已订阅");
        }
        
        // 检查订阅数量限制
        long subscriptionCount = subscriptionRepository.countByUserId(userId);
        if (subscriptionCount >= 5) {
            throw new BusinessException("SUBSCRIPTION_LIMIT_EXCEEDED", "最多只能订阅5个主题");
        }
        
        // 创建新订阅
        Subscription subscription = new Subscription();
        subscription.setUserId(userId);
        subscription.setTopicName(request.getTopicName());
        subscription.setSubscribedAt(LocalDateTime.now());
        
        Subscription saved = subscriptionRepository.insert(subscription);
        
        SubscriptionResponses.CreateSubscriptionResponse response = new SubscriptionResponses.CreateSubscriptionResponse();
        response.setId(saved.getId());
        response.setUserId(saved.getUserId());
        response.setTopicName(saved.getTopicName());
        response.setSubscribedAt(saved.getSubscribedAt());
        
        return response;
    }
    
    // 删除用户订阅关系
    public void deleteSubscription(Long userId, Long subscriptionId) {
        // 验证用户是否存在
        if (!userRepository.existsById(userId)) {
            throw new BusinessException("USER_NOT_FOUND", "用户不存在");
        }
        
        // 验证订阅记录是否存在且属于该用户
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findByUserIdAndId(userId, subscriptionId);
        if (!subscriptionOpt.isPresent()) {
            throw new BusinessException("SUBSCRIPTION_NOT_FOUND", "订阅记录不存在");
        }
        
        // 删除订阅记录
        subscriptionRepository.deleteById(subscriptionId);
    }
}
