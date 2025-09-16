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
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService service;

    public SubscriptionController(SubscriptionService service) {
        this.service = service;
    }

    // 获取用户订阅列表
    @GetMapping
    public ResponseEntity<List<SubscriptionResponses.SubscriptionItem>> getSubscriptions(
            @RequestParam Long userId) {
        List<SubscriptionResponses.SubscriptionItem> result = service.getSubscriptions(userId);
        return ResponseEntity.ok(result);
    }

    // 创建订阅
    @PostMapping
    public ResponseEntity<SubscriptionResponses.CreateSubscriptionResponse> createSubscription(
            @RequestBody SubscriptionRequests.CreateSubscriptionRequest request) {
        Subscription subscription = service.createSubscription(request);
        SubscriptionResponses.CreateSubscriptionResponse response = new SubscriptionResponses.CreateSubscriptionResponse();
        response.setId(subscription.getId());
        response.setUserId(subscription.getUserId());
        response.setTopicName(subscription.getTopicName());
        response.setSubscribedAt(subscription.getSubscribedAt());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 删除订阅
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        service.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }
}
