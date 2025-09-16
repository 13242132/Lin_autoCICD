package com.example.demo.api.repository;

import com.example.demo.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicApiRepository extends JpaRepository<Topic, Long>, JpaSpecificationExecutor<Topic> {
}
