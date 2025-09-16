package com.example.ai.airepair.constant;

import java.util.List;

/**
 * CRUD接口导入常量
 */
public class CurdImportConstants {
    
    /**
     * CRUD接口常用导入白名单
     */
    public static final List<String> REPOSITORY_IMPORT_WHITELIST = List.of(
        "import java.util.List;",
        "import jakarta.persistence.*;",
        "import java.util.Optional;"
    );
}