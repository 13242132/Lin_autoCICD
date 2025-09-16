package com.example.demo.api.controller;

import com.example.demo.entity.Like;
import com.example.demo.api.querydto.LikeQueryDTO;
import com.example.demo.api.service.LikeApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/likes")
public class LikeApiController {

    @Autowired
    private LikeApiService service;

    @GetMapping
    public List<Like> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Like> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Like save(@RequestBody Like entity) {
        return service.save(entity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/query")
    public List<Like> queryByConditions(@RequestBody LikeQueryDTO queryDTO) {
        return service.queryByConditions(queryDTO);
    }
}
