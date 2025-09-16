package com.example.demo.repository;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import jakarta.persistence.*;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Topic;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    // 单表：获取所有主题分类
    @Query("SELECT t FROM Topic t")
    List<Topic> findAllTopics();

}
