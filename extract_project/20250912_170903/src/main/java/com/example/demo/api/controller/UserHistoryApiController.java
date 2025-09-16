package com.example.demo.api.controller;

import com.example.demo.entity.UserHistory;
import com.example.demo.api.querydto.UserHistoryQueryDTO;
import com.example.demo.api.service.UserHistoryApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/userhistorys")
public class UserHistoryApiController {

    @Autowired
    private UserHistoryApiService service;

    @GetMapping
    public List<UserHistory> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserHistory> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public UserHistory save(@RequestBody UserHistory entity) {
        return service.save(entity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/query")
    public List<UserHistory> queryByConditions(@RequestBody UserHistoryQueryDTO queryDTO) {
        return service.queryByConditions(queryDTO);
    }
}
