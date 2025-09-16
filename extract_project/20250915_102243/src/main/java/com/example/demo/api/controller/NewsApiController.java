package com.example.demo.api.controller;

import com.example.demo.entity.News;
import com.example.demo.api.querydto.NewsQueryDTO;
import com.example.demo.api.service.NewsApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/newss")
public class NewsApiController {

    @Autowired
    private NewsApiService service;

    @GetMapping
    public List<News> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<News> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public News save(@RequestBody News entity) {
        return service.save(entity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/query")
    public List<News> queryByConditions(@RequestBody NewsQueryDTO queryDTO) {
        return service.queryByConditions(queryDTO);
    }
}
