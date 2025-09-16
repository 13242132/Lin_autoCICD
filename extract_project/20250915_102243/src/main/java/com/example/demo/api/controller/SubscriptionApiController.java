package com.example.demo.api.controller;

import com.example.demo.entity.Subscription;
import com.example.demo.api.querydto.SubscriptionQueryDTO;
import com.example.demo.api.service.SubscriptionApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/subscriptions")
public class SubscriptionApiController {

    @Autowired
    private SubscriptionApiService service;

    @GetMapping
    public List<Subscription> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subscription> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Subscription save(@RequestBody Subscription entity) {
        return service.save(entity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/query")
    public List<Subscription> queryByConditions(@RequestBody SubscriptionQueryDTO queryDTO) {
        return service.queryByConditions(queryDTO);
    }
}
