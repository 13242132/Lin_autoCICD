package com.example.demo.repository;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import jakarta.persistence.*;
import org.springframework.data.domain.Pageable;

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
    List<News> findAllOrderedByPublishedAtDesc(Pageable pageable);

    // 单表：根据分类和分页查询新闻列表
    @Query("SELECT n FROM News n WHERE (:category IS NULL OR n.source = :category) ORDER BY n.publishedAt DESC")
    List<News> findByCategoryAndPageable(@Param("category") String category, Pageable pageable);

    // 单表：获取新闻总数（用于分页计算）
    @Query("SELECT COUNT(n) FROM News n")
    long countAll();

    // 单表：根据分类获取新闻总数
    @Query("SELECT COUNT(n) FROM News n WHERE (:category IS NULL OR n.source = :category)")
    long countByCategory(@Param("category") String category);

    // 单表：根据ID查询新闻
    @Query("SELECT n FROM News n WHERE n.id = :id")
    Optional<News> findById(@Param("id") Long id);

    // 单表：保存新闻
    default News saveNews(News news) {
        return save(news);
    }

    // 单表：删除新闻
    default void deleteNewsById(Long id) {
        deleteById(id);
    }

}
