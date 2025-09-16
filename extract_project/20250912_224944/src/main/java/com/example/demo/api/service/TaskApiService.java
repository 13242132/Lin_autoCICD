package com.example.demo.api.service;

import com.example.demo.entity.Task;
import com.example.demo.api.querydto.TaskQueryDTO;
import java.util.List;
import java.util.Optional;

public interface TaskApiService {
    List<Task> findAll();
    Optional<Task> findById(Long id);
    Task save(Task entity);
    void deleteById(Long id);
    List<Task> queryByConditions(TaskQueryDTO queryDTO);
}
