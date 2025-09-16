// Class: Like
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
@Table(name = "like_tbl")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "news_id", nullable = false)
    private Long newsId;

    @Column(name = "liked_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime likedAt = LocalDateTime.now();
}
