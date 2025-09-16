package com.example.demo.controller;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Subscription;
import com.example.demo.request.SubscriptionRequests;
import com.example.demo.response.SubscriptionResponses;
import com.example.demo.service.SubscriptionService;

@RestController
@RequestMapping("/api/users/{userId}/subscriptions")
public class SubscriptionController {
    
    private final SubscriptionService subscriptionService;
    
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }
    
    // 获取用户订阅主题列表
    @GetMapping
    public ResponseEntity<List<SubscriptionResponses.SubscriptionItem>> getSubscriptions(
            @PathVariable Long userId) {
        List<SubscriptionResponses.SubscriptionItem> subscriptions = subscriptionService.getSubscriptionsByUserId(userId);
        return ResponseEntity.ok(subscriptions);
    }
    
    // 创建用户订阅关系
    @PostMapping
    public ResponseEntity<SubscriptionResponses.CreateSubscriptionResponse> createSubscription(
            @PathVariable Long userId,
            @RequestBody SubscriptionRequests.CreateSubscriptionRequest request) {
        SubscriptionResponses.CreateSubscriptionResponse response = subscriptionService.createSubscription(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // 删除用户订阅关系
    @DeleteMapping("/{subscriptionId}")
    public ResponseEntity<Void> deleteSubscription(
            @PathVariable Long userId,
            @PathVariable Long subscriptionId) {
        subscriptionService.deleteSubscription(userId, subscriptionId);
        return ResponseEntity.noContent().build();
    }
}
