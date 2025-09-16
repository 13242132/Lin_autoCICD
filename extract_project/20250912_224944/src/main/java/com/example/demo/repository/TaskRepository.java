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

import com.example.demo.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // 单表：创建任务
    default Task create(Task task) {
        return save(task);
    }

    // 单表：根据ID查找任务
    @Query("SELECT t FROM Task t WHERE t.id = :id")
    Optional<Task> findById(@Param("id") Long id);

    // 单表：根据状态查询任务列表
    @Query("SELECT t FROM Task t WHERE t.status = :status")
    List<Task> findByStatus(@Param("status") String status);

    // 单表：分页查询任务列表（按状态）
    @Query("SELECT t FROM Task t WHERE t.status = :status")
    Page<Task> findByStatus(@Param("status") String status, Pageable pageable);

    // 单表：查询所有任务（分页）
    @Query("SELECT t FROM Task t")
    Page<Task> findAll(Pageable pageable);

    // 单表：更新任务状态
    @Query("UPDATE Task t SET t.status = :status, t.updatedAt = :updatedAt WHERE t.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") String status, @Param("updatedAt") LocalDateTime updatedAt);

    // 单表：查询指定用户分配的任务
    @Query("SELECT t FROM Task t WHERE t.assignee = :assignee")
    List<Task> findByAssignee(@Param("assignee") String assignee);

    // 单表：根据ID删除任务
    default void deleteById(Long id) {
        deleteById(id);
    }
}
