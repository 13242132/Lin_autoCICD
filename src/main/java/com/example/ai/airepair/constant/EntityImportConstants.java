package com.example.ai.airepair.constant;

import java.util.List;

/**
 * 实体类导入白名单常量
 */
public class EntityImportConstants {
    
    /**
     * 实体类常用导入白名单
     */
    public static final List<String> ENTITY_IMPORT_WHITELIST = List.of(
        "import jakarta.persistence.*;",
        "import java.time.LocalDateTime;",
        "import java.math.BigDecimal;",
        "import java.io.Serializable;",
        "import com.fasterxml.jackson.annotation.JsonFormat;",
        "import lombok.Data;",
        "import java.util.List;"
    );
}