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

    private final SubscriptionService service;

    public SubscriptionController(SubscriptionService service) {
        this.service = service;
    }

    // 获取用户订阅主题列表
    @GetMapping
    public ResponseEntity<?> getSubscriptions(@PathVariable Long userId) {
        try {
            List<Subscription> subscriptions = service.getSubscriptions(userId);
            // 构造响应体
            List<SubscriptionResponses.GetSubscriptionsResponse.TopicItem> topicItems = 
                subscriptions.stream()
                    .map(sub -> new SubscriptionResponses.GetSubscriptionsResponse.TopicItem(
                        sub.getTopicName(),
                        sub.getSubscribedAt().toString()
                    ))
                    .toList();

            SubscriptionResponses.GetSubscriptionsResponse response = 
                new SubscriptionResponses.GetSubscriptionsResponse(userId, topicItems, subscriptions.size());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if ("USER_NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new SubscriptionResponses.ErrorResponse("USER_NOT_FOUND", "用户不存在"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new SubscriptionResponses.ErrorResponse("INTERNAL_ERROR", "服务器内部错误"));
        }
    }

    // 创建用户订阅
    @PostMapping
    public ResponseEntity<?> createSubscription(@PathVariable Long userId, @RequestBody SubscriptionRequests.CreateSubscriptionRequest request) {
        try {
            Subscription subscription = service.createSubscription(userId, request);
            SubscriptionResponses.CreateSubscriptionResponse response =
                new SubscriptionResponses.CreateSubscriptionResponse(
                    subscription.getId(),
                    subscription.getUserId(),
                    subscription.getTopicName(),
                    subscription.getSubscribedAt().toString()
                );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            if ("SUBSCRIPTION_LIMIT_EXCEEDED".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new SubscriptionResponses.ErrorResponse("SUBSCRIPTION_LIMIT_EXCEEDED", "最多可订阅5个主题"));
            } else if ("USER_NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new SubscriptionResponses.ErrorResponse("USER_NOT_FOUND", "用户不存在"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new SubscriptionResponses.ErrorResponse("INTERNAL_ERROR", "服务器内部错误"));
        }
    }

    // 删除用户订阅
    @DeleteMapping("/{topicName}")
    public ResponseEntity<?> deleteSubscription(@PathVariable Long userId, @PathVariable String topicName) {
        try {
            service.deleteSubscription(userId, topicName);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if ("SUBSCRIPTION_NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new SubscriptionResponses.ErrorResponse("SUBSCRIPTION_NOT_FOUND", "订阅记录不存在"));
            } else if ("USER_NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new SubscriptionResponses.ErrorResponse("USER_NOT_FOUND", "用户不存在"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new SubscriptionResponses.ErrorResponse("INTERNAL_ERROR", "服务器内部错误"));
        }
    }
}
