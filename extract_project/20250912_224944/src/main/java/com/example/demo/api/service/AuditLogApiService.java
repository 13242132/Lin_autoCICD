package com.example.demo.api.service;

import com.example.demo.entity.AuditLog;
import com.example.demo.api.querydto.AuditLogQueryDTO;
import java.util.List;
import java.util.Optional;

public interface AuditLogApiService {
    List<AuditLog> findAll();
    Optional<AuditLog> findById(Long id);
    AuditLog save(AuditLog entity);
    void deleteById(Long id);
    List<AuditLog> queryByConditions(AuditLogQueryDTO queryDTO);
}
