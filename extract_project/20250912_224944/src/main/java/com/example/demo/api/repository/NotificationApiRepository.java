package com.example.demo.api.repository;

import com.example.demo.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationApiRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {
}
