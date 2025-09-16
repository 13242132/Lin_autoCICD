package com.example.demo.api.service;

import com.example.demo.entity.Subscription;
import com.example.demo.api.querydto.SubscriptionQueryDTO;
import java.util.List;
import java.util.Optional;

public interface SubscriptionApiService {
    List<Subscription> findAll();
    Optional<Subscription> findById(Long id);
    Subscription save(Subscription entity);
    void deleteById(Long id);
    List<Subscription> queryByConditions(SubscriptionQueryDTO queryDTO);
}
