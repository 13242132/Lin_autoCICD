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

import com.example.demo.entity.UserHistory;

import com.example.demo.service.UserHistoryService;

import com.example.demo.request.UserHistoryRequests;

import com.example.demo.response.UserHistoryResponses;

@RestController
@RequestMapping("/api/users/{userId}/history")
public class UserHistoryController {

    private final UserHistoryService service;

    public UserHistoryController(UserHistoryService service) {
        this.service = service;
    }

    // 更新用户阅读历史
    @PostMapping
    public ResponseEntity<?> updateHistory(@PathVariable Long userId, @RequestBody UserHistoryRequests.UpdateUserHistoryRequest request) {
        try {
            UserHistory history = service.updateUserHistory(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(new UserHistoryResponses.UpdateUserHistoryResponse(
                history.getId(),
                history.getUserId(),
                history.getNewsId(),
                history.getTitle(),
                history.getSource(),
                history.getReadAt().toString()
            ));
        } catch (RuntimeException e) {
            if ("INVALID_NEWS_ID".equals(e.getMessage())) {
                return ResponseEntity.badRequest().body(new ErrorResponse("INVALID_NEWS_ID", "新闻ID无效"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 获取用户阅读历史
    @GetMapping
    public ResponseEntity<?> getHistory(@PathVariable Long userId, @RequestParam(required = false, defaultValue = "1") Integer page,
                                        @RequestParam(required = false, defaultValue = "10") Integer size) {
        UserHistoryRequests.GetUserHistoryRequest req = new UserHistoryRequests.GetUserHistoryRequest(page, size);
        List<UserHistory> histories = service.getUserHistory(userId, req);

        // 构造返回结果
        List<UserHistoryResponses.GetUserHistoryResponse.UserHistoryItem> items = histories.stream()
            .map(h -> new UserHistoryResponses.GetUserHistoryResponse.UserHistoryItem(
                h.getId(),
                h.getUserId(),
                h.getNewsId(),
                h.getTitle(),
                h.getSource(),
                h.getReadAt().toString()
            )).toList();

        return ResponseEntity.ok(new UserHistoryResponses.GetUserHistoryResponse(
            items,
            (long) histories.size(),
            page,
            size
        ));
    }

    // 错误响应类
    private static class ErrorResponse {
        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() { return error; }
        public String getMessage() { return message; }
    }
}
