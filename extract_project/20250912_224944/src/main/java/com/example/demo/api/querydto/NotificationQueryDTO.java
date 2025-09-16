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
public class NotificationQueryDTO {

    private Long id;
    private Long userId;
    private String type;
    private String content;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;

}
