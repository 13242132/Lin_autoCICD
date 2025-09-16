package com.example.demo.repository;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import jakarta.persistence.*;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 单表：插入评论
    default Comment insert(Comment entity) {
        return save(entity);
    }

    // 单表：根据ID查询评论
    @Query("SELECT c FROM Comment c WHERE c.id = :id")
    Optional<Comment> findById(@Param("id") Long id);

    // 单表：根据新闻ID查询评论列表
    @Query("SELECT c FROM Comment c WHERE c.newsId = :newsId ORDER BY c.createdAt DESC")
    List<Comment> findByNewsId(@Param("newsId") Long newsId);

    // 单表：根据用户ID查询评论列表
    @Query("SELECT c FROM Comment c WHERE c.userId = :userId ORDER BY c.createdAt DESC")
    List<Comment> findByUserId(@Param("userId") Long userId);

    // 单表：根据新闻ID和用户ID查询评论
    @Query("SELECT c FROM Comment c WHERE c.newsId = :newsId AND c.userId = :userId")
    Optional<Comment> findByNewsIdAndUserId(@Param("newsId") Long newsId, @Param("userId") Long userId);

    // 单表：根据新闻ID分页查询评论
    @Query("SELECT c FROM Comment c WHERE c.newsId = :newsId ORDER BY c.createdAt DESC")
    Page<Comment> findByNewsIdWithPagination(@Param("newsId") Long newsId, Pageable pageable);

    // 单表：删除评论（JPA自带deleteById已满足，此处仅做语义封装）
    default void deleteById(Long id) {
        deleteById(id);
    }
}
