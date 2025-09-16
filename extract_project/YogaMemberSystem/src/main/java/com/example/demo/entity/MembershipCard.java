// Class: MembershipCard
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
@Table(name = "membership_card_tbl")
public class MembershipCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "card_number", nullable = false, length = 20, unique = true)
    private String cardNumber;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "status_update_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime statusUpdateTime = LocalDateTime.now();

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt = LocalDateTime.now();

}
