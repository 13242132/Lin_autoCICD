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

    // 单表：根据分类和来源查询新闻列表
    @Query("SELECT n FROM News n WHERE (:category IS NULL OR n.categoryId = :category) AND (:source IS NULL OR n.source = :source)")
    Page<News> findByCategoryAndSource(@Param("category") Integer category, @Param("source") String source, Pageable pageable);

    // 单表：根据ID查询新闻
    @Query("SELECT n FROM News n WHERE n.id = :id")
    Optional<News> findById(@Param("id") Long id);

    // 单表：插入新闻
    default News insert(News entity) {
        return save(entity);
    }

    // 单表：删除新闻
    default void deleteById(Long id) {
        deleteById(id);
    }
}
