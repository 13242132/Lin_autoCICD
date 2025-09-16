package com.example.ai.airepair.constant;

import java.util.List;

/**
 * 通用导入修复常量
 */
public class ImportFixConstants {
    
    /**
     * 通用导入白名单
     */
    public static final List<String> GENERAL_IMPORT_WHITELIST = List.of(
        "import java.util.List;",
        "import java.util.Optional;",
        "import java.util.Map;",
        "import java.util.Set;",
        "import java.time.LocalDateTime;",
        "import org.springframework.http.ResponseEntity;",
        "import org.springframework.http.HttpStatus;",
        "import org.springframework.web.bind.annotation.*;",
        "import java.util.ArrayList;",
        "import java.util.HashMap;",
        "import java.util.HashSet;",
        "import java.time.LocalDate;",
        "import java.time.LocalTime;",
        "import java.time.format.DateTimeFormatter;",
        "import lombok.Data;",
        "import lombok.Builder;",
        "import lombok.NoArgsConstructor;",
        "import lombok.AllArgsConstructor;",
        "import org.springframework.stereotype.Service;",
        "import org.springframework.stereotype.Repository;",
        "import org.springframework.stereotype.Component;",
        "import org.springframework.beans.factory.annotation.Autowired;"
    );
}