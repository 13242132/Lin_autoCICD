// Class: News
package com.example.demo.entity;
import java.time.LocalDateTime;
import java.math.BigDecimal;


import jakarta.persistence.*;
import lombok.Data;
import java.time.*;
import java.math.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "news_tbl")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "source", nullable = false)
    private String source;

    @Column(name = "summary")
    private String summary;

    @Column(name = "published_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime publishedAt;

    @Column(name = "url", length = 500)
    private String url;
}
