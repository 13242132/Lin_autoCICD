package com.example.demo.api.controller;

import com.example.demo.entity.User;
import com.example.demo.api.querydto.UserQueryDTO;
import com.example.demo.api.service.UserApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/users")
public class UserApiController {

    @Autowired
    private UserApiService service;

    @GetMapping
    public List<User> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public User save(@RequestBody User entity) {
        return service.save(entity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/query")
    public List<User> queryByConditions(@RequestBody UserQueryDTO queryDTO) {
        return service.queryByConditions(queryDTO);
    }
}
