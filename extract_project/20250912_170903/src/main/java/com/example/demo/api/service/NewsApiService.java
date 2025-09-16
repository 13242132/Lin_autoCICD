package com.example.demo.api.service;

import com.example.demo.entity.News;
import com.example.demo.api.querydto.NewsQueryDTO;
import java.util.List;
import java.util.Optional;

public interface NewsApiService {
    List<News> findAll();
    Optional<News> findById(Long id);
    News save(News entity);
    void deleteById(Long id);
    List<News> queryByConditions(NewsQueryDTO queryDTO);
}
