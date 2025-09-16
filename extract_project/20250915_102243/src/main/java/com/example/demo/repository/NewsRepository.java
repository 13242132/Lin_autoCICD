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

import com.example.demo.entity.News;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    // 单表：根据分类查询新闻列表
    @Query("SELECT n FROM News n WHERE (:category IS NULL OR n.source = :category)")
    Page<News> findByCategory(@Param("category") String category, Pageable pageable);

    // 单表：查询所有新闻（分页）
    @Query("SELECT n FROM News n")
    Page<News> findAll(Pageable pageable);

    // 单表：根据ID查询新闻
    @Query("SELECT n FROM News n WHERE n.id = :id")
    Optional<News> findById(@Param("id") Long id);

    // 单表：根据分类和发布时间排序查询新闻
    @Query("SELECT n FROM News n WHERE (:category IS NULL OR n.source = :category) ORDER BY n.publishedAt DESC")
    Page<News> findByCategoryOrderByPublishedAtDesc(@Param("category") String category, Pageable pageable);

    // 单表：根据分类统计新闻数量
    @Query("SELECT COUNT(n) FROM News n WHERE (:category IS NULL OR n.source = :category)")
    long countByCategory(@Param("category") String category);
}
