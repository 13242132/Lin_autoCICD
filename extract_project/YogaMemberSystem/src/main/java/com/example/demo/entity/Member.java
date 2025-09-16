// Class: Member
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
@Table(name = "member_tbl")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "phone", nullable = false, length = 11, unique = true)
    private String phone;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "avatar", length = 255)
    private String avatar;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "register_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate registerTime;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt = LocalDateTime.now();

}
