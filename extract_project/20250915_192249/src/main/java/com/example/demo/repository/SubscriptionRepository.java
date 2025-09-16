package com.example.demo.repository;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import jakarta.persistence.*;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Subscription;
import com.example.demo.entity.User;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    
    // 单表：根据用户ID查询订阅列表
    @Query("SELECT s FROM Subscription s WHERE s.userId = :userId")
    List<Subscription> findByUserId(@Param("userId") Long userId);
    
    // 单表：根据用户ID和主题名查询订阅记录是否存在
    @Query("SELECT s FROM Subscription s WHERE s.userId = :userId AND s.topicName = :topicName")
    Optional<Subscription> findByUserIdAndTopicName(@Param("userId") Long userId, @Param("topicName") String topicName);
    
    // 单表：根据用户ID统计订阅数量
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.userId = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    // 单表：根据用户ID和订阅ID查询订阅记录
    @Query("SELECT s FROM Subscription s WHERE s.userId = :userId AND s.id = :subscriptionId")
    Optional<Subscription> findByUserIdAndId(@Param("userId") Long userId, @Param("subscriptionId") Long subscriptionId);
    
    // 单表：插入订阅记录
    default Subscription insert(Subscription entity) {
        return save(entity);
    }
    
    // 单表：删除订阅记录
    default void deleteByUserIdAndId(Long userId, Long subscriptionId) {
        deleteByUserIdAndId(userId, subscriptionId);
    }
}
