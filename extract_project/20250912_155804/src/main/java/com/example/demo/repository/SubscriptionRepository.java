package com.example.demo.repository;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import jakarta.persistence.*;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Subscription;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    
    // 单表：根据用户ID查询订阅记录
    @Query("SELECT s FROM Subscription s WHERE s.userId = :userId")
    List<Subscription> findByUserId(@Param("userId") Long userId);
    
    // 单表：根据用户ID和主题名查询订阅记录
    @Query("SELECT s FROM Subscription s WHERE s.userId = :userId AND s.topicName = :topicName")
    Optional<Subscription> findByUserIdAndTopicName(@Param("userId") Long userId, @Param("topicName") String topicName);
    
    // 单表：根据用户ID删除所有订阅记录
    @Query("DELETE FROM Subscription s WHERE s.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
    
    // 单表：统计用户订阅数
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.userId = :userId")
    long countByUserId(@Param("userId") Long userId);
}
