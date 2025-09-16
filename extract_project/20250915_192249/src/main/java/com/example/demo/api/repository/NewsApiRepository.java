package com.example.demo.api.repository;

import com.example.demo.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsApiRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {
}
