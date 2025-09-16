package com.example.demo.api.controller;

import com.example.demo.entity.Topic;
import com.example.demo.api.querydto.TopicQueryDTO;
import com.example.demo.api.service.TopicApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/topics")
public class TopicApiController {

    @Autowired
    private TopicApiService service;

    @GetMapping
    public List<Topic> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Topic> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Topic save(@RequestBody Topic entity) {
        return service.save(entity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/query")
    public List<Topic> queryByConditions(@RequestBody TopicQueryDTO queryDTO) {
        return service.queryByConditions(queryDTO);
    }
}
