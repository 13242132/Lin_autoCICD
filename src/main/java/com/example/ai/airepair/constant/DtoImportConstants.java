package com.example.ai.airepair.constant;

import java.util.List;

/**
 * DTO类导入白名单常量
 */
public class DtoImportConstants {
    
    /**
     * DTO类常用导入白名单
     */
    public static final List<String> DTO_IMPORT_WHITELIST = List.of(
        "import lombok.Data;",
        "import java.time.LocalDateTime;",
        "import java.time.LocalDate;",
        "import java.math.BigDecimal;",
        "import java.io.Serializable;",
        "import com.fasterxml.jackson.annotation.JsonFormat;",
        "import java.util.List;",
        "import java.util.ArrayList;",
        "import java.util.Optional;"
    );
}