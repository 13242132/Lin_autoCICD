package com.example.demo.api.service;

import com.example.demo.entity.Topic;
import com.example.demo.api.querydto.TopicQueryDTO;
import java.util.List;
import java.util.Optional;

public interface TopicApiService {
    List<Topic> findAll();
    Optional<Topic> findById(Long id);
    Topic save(Topic entity);
    void deleteById(Long id);
    List<Topic> queryByConditions(TopicQueryDTO queryDTO);
}
