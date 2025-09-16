package com.example.demo.controller;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.response.TopicResponses;
import com.example.demo.service.TopicService;

@RequestMapping("/api/topics")
@RestController
public class TopicController {

    private final TopicService service;

    public TopicController(TopicService service) {
        this.service = service;
    }

    // 获取所有主题
    @GetMapping
    public ResponseEntity<List<TopicResponses.TopicItem>> getTopics() {
        List<TopicResponses.TopicItem> topics = service.getAllTopics();
        return ResponseEntity.ok(topics);
    }
}
