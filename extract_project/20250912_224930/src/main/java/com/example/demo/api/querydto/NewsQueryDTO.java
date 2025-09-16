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
public class NewsQueryDTO {

    private Long id;
    private String title;
    private String source;
    private String summary;
    private LocalDateTime publishedAt;
    private String url;
    private String category;

}
