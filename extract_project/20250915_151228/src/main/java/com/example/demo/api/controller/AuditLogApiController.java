package com.example.demo.api.controller;

import com.example.demo.entity.AuditLog;
import com.example.demo.api.querydto.AuditLogQueryDTO;
import com.example.demo.api.service.AuditLogApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/auditlogs")
public class AuditLogApiController {

    @Autowired
    private AuditLogApiService service;

    @GetMapping
    public List<AuditLog> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLog> findById(@PathVariable Long id) {
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public AuditLog save(@RequestBody AuditLog entity) {
        return service.save(entity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/query")
    public List<AuditLog> queryByConditions(@RequestBody AuditLogQueryDTO queryDTO) {
        return service.queryByConditions(queryDTO);
    }
}
