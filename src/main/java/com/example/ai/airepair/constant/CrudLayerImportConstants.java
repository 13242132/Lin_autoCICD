package com.example.ai.airepair.constant;

import java.util.List;

/**
 * CRUD三层架构导入常量
 */
public class CrudLayerImportConstants {
    
    /**
     * Repository层常用导入白名单
     */
    public static final List<String> REPOSITORY_IMPORT_WHITELIST = List.of(
        "import java.util.List;",
        "import java.util.Optional;",
        "import java.util.stream.Collectors;",
        "import java.time.LocalDateTime;",
        "import java.time.format.DateTimeFormatter;",
        "import org.springframework.data.jpa.repository.JpaRepository;",
        "import org.springframework.data.jpa.repository.JpaSpecificationExecutor;",
        "import org.springframework.stereotype.Repository;",
        "import jakarta.persistence.*;"
    );
    
    /**
     * Service层常用导入白名单
     */
    public static final List<String> SERVICE_IMPORT_WHITELIST = List.of(
        "import java.util.List;",
        "import java.util.Optional;",
        "import java.util.stream.Collectors;",
        "import org.springframework.stereotype.Service;",
        "import org.springframework.beans.factory.annotation.Autowired;",
        "import org.springframework.data.domain.Page;",
        "import org.springframework.data.domain.Pageable;",
        "import org.springframework.data.jpa.domain.Specification;",
        "import jakarta.persistence.criteria.Predicate;",
        "import java.util.ArrayList;",
        "import java.time.LocalDateTime;",
        "import java.time.format.DateTimeFormatter;"
    );
    
    /**
     * Controller层常用导入白名单
     */
    public static final List<String> CONTROLLER_IMPORT_WHITELIST = List.of(
        "import java.util.List;",
        "import java.util.Optional;",
        "import java.util.stream.Collectors;",
        "import java.time.LocalDateTime;",
        "import java.time.format.DateTimeFormatter;",
        "import org.springframework.beans.factory.annotation.Autowired;",
        "import org.springframework.http.ResponseEntity;",
        "import org.springframework.web.bind.annotation.*;",
        "import org.springframework.web.bind.annotation.RequestParam;",
        "import org.springframework.web.bind.annotation.PathVariable;",
        "import org.springframework.web.bind.annotation.RequestBody;",
        "import org.springframework.http.HttpStatus;"
    );
    
    /**
     * 请求类常用导入白名单
     */
    public static final List<String> REQUEST_IMPORT_WHITELIST = List.of(
        "import java.util.List;",
        "import java.util.Optional;",
        "import java.time.LocalDateTime;",
        "import java.time.format.DateTimeFormatter;",
        "import java.math.BigDecimal;"
    );
    
    /**
     * 返回类常用导入白名单
     */
    public static final List<String> RESPONSE_IMPORT_WHITELIST = List.of(
        "import java.util.List;",
        "import java.util.Optional;",
        "import java.time.LocalDateTime;",
        "import java.time.format.DateTimeFormatter;",
        "import java.math.BigDecimal;"
    );
}