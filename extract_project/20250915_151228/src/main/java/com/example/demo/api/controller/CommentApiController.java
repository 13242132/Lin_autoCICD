package com.example.demo.api.controller;

import com.example.demo.entity.Comment;
import com.example.demo.api.querydto.CommentQueryDTO;
import com.example.demo.api.service.CommentApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/comments")
public class CommentApiController {

    @Autowired
    private CommentApiService service;

    @GetMapping
    public List<Comment> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Comment save(@RequestBody Comment entity) {
        return service.save(entity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/query")
    public List<Comment> queryByConditions(@RequestBody CommentQueryDTO queryDTO) {
        return service.queryByConditions(queryDTO);
    }
}
