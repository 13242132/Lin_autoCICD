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

import com.example.demo.entity.UserHistory;

@Repository
public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {

    // 单表：插入用户阅读历史
    default UserHistory insert(UserHistory entity) {
        return save(entity);
    }

    // 单表：根据用户ID和新闻ID查询阅读历史
    @Query("SELECT uh FROM UserHistory uh WHERE uh.userId = :userId AND uh.newsId = :newsId")
    Optional<UserHistory> findByUserIdAndNewsId(@Param("userId") Long userId, @Param("newsId") Long newsId);

    // 单表：根据用户ID分页查询阅读历史
    @Query("SELECT uh FROM UserHistory uh WHERE uh.userId = :userId ORDER BY uh.readAt DESC")
    List<UserHistory> findByUserId(@Param("userId") Long userId);

    // 单表：删除用户阅读历史（JPA自带deleteById已满足）
    default void deleteById(Long id) {
        deleteById(id);
    }
}
