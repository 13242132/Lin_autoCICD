package com.example.demo.api.repository;

import com.example.demo.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskApiRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
}
