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
import com.example.demo.exception.UnifiedException;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService service;

    public SubscriptionController(SubscriptionService service) {
        this.service = service;
    }

    // 获取用户订阅主题列表
    @GetMapping
    public ResponseEntity<?> getSubscriptions(@RequestParam Long userId) {
        try {
            SubscriptionRequests.GetSubscriptionsRequest request = new SubscriptionRequests.GetSubscriptionsRequest();
            request.setUserId(userId);
            List<SubscriptionResponses.SubscriptionItem> result = service.getSubscriptions(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new UnifiedException("获取用户订阅主题列表失败: " + e.getMessage());
        }
    }

    // 创建用户主题订阅
    @PostMapping
    public ResponseEntity<?> createSubscription(@RequestBody SubscriptionRequests.CreateSubscriptionRequest request) {
        try {
            SubscriptionResponses.CreateSubscriptionResponse response = service.createSubscription(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            throw new UnifiedException("创建用户主题订阅失败: " + e.getMessage());
        }
    }

    // 删除用户主题订阅
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubscription(@PathVariable Long id) {
        try {
            service.deleteSubscription(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new UnifiedException("删除用户主题订阅失败: " + e.getMessage());
        }
    }
}
