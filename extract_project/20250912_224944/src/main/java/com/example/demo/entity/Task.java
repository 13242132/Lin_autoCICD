// Class: Task
package com.example.demo.entity;
import java.time.LocalDateTime;
import java.math.BigDecimal;


import jakarta.persistence.*;
import lombok.Data;
import java.time.*;
import java.math.*;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.List;

@Data
@Entity
@Table(name = "task_tbl")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "priority", nullable = false, length = 10)
    private String priority;

    @Column(name = "due_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime dueDate;

    @Column(name = "assignee", nullable = false, length = 50)
    private String assignee;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedAt;
}
