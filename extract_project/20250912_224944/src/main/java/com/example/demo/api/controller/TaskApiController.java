package com.example.demo.api.controller;

import com.example.demo.entity.Task;
import com.example.demo.api.querydto.TaskQueryDTO;
import com.example.demo.api.service.TaskApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/tasks")
public class TaskApiController {

    @Autowired
    private TaskApiService service;

    @GetMapping
    public List<Task> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Task save(@RequestBody Task entity) {
        return service.save(entity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/query")
    public List<Task> queryByConditions(@RequestBody TaskQueryDTO queryDTO) {
        return service.queryByConditions(queryDTO);
    }
}
