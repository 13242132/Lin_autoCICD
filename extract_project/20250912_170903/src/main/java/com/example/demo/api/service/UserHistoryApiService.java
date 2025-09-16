package com.example.demo.api.service;

import com.example.demo.entity.UserHistory;
import com.example.demo.api.querydto.UserHistoryQueryDTO;
import java.util.List;
import java.util.Optional;

public interface UserHistoryApiService {
    List<UserHistory> findAll();
    Optional<UserHistory> findById(Long id);
    UserHistory save(UserHistory entity);
    void deleteById(Long id);
    List<UserHistory> queryByConditions(UserHistoryQueryDTO queryDTO);
}
