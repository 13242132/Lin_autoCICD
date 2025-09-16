package com.example.demo.api.service;

import com.example.demo.entity.Like;
import com.example.demo.api.querydto.LikeQueryDTO;
import java.util.List;
import java.util.Optional;

public interface LikeApiService {
    List<Like> findAll();
    Optional<Like> findById(Long id);
    Like save(Like entity);
    void deleteById(Long id);
    List<Like> queryByConditions(LikeQueryDTO queryDTO);
}
