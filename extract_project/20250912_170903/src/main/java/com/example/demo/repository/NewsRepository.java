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

import com.example.demo.entity.News;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    // 单表：根据分类查询新闻列表
    @Query("SELECT n FROM News n WHERE (:category IS NULL OR n.source = :category) ORDER BY n.publishedAt DESC")
    List<News> findByCategory(@Param("category") String category);

    // 单表：分页查询新闻列表
    @Query("SELECT n FROM News n ORDER BY n.publishedAt DESC")
    List<News> findAllOrderedByPublishedAt(@Param("page") int page, @Param("size") int size);

    // 单表：根据分类和分页查询新闻列表
    @Query("SELECT n FROM News n WHERE (:category IS NULL OR n.source = :category) ORDER BY n.publishedAt DESC")
    List<News> findByCategoryAndPage(@Param("category") String category, @Param("page") int page, @Param("size") int size);

    // 单表：查询新闻总数
    @Query("SELECT COUNT(n) FROM News n")
    long countAllNews();

    // 单表：根据分类查询新闻总数
    @Query("SELECT COUNT(n) FROM News n WHERE (:category IS NULL OR n.source = :category)")
    long countNewsByCategory(@Param("category") String category);

}
