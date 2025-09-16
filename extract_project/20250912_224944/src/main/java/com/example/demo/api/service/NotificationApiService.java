package com.example.demo.api.service;

import com.example.demo.entity.Notification;
import com.example.demo.api.querydto.NotificationQueryDTO;
import java.util.List;
import java.util.Optional;

public interface NotificationApiService {
    List<Notification> findAll();
    Optional<Notification> findById(Long id);
    Notification save(Notification entity);
    void deleteById(Long id);
    List<Notification> queryByConditions(NotificationQueryDTO queryDTO);
}
