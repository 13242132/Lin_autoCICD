package com.example.demo.api.querydto;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;


@Data
public class UserQueryDTO {

    private Long id;
    private String username;
    private String email;
    private String password;
    private String avatar;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

}
