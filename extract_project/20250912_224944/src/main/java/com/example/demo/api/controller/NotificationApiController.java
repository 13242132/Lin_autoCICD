package com.example.demo.api.controller;

import com.example.demo.entity.Notification;
import com.example.demo.api.querydto.NotificationQueryDTO;
import com.example.demo.api.service.NotificationApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/notifications")
public class NotificationApiController {

    @Autowired
    private NotificationApiService service;

    @GetMapping
    public List<Notification> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Notification save(@RequestBody Notification entity) {
        return service.save(entity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/query")
    public List<Notification> queryByConditions(@RequestBody NotificationQueryDTO queryDTO) {
        return service.queryByConditions(queryDTO);
    }
}
