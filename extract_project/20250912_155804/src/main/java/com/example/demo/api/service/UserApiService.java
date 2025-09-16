package com.example.demo.api.service;

import com.example.demo.entity.User;
import com.example.demo.api.querydto.UserQueryDTO;
import java.util.List;
import java.util.Optional;

public interface UserApiService {
    List<User> findAll();
    Optional<User> findById(Long id);
    User save(User entity);
    void deleteById(Long id);
    List<User> queryByConditions(UserQueryDTO queryDTO);
}
