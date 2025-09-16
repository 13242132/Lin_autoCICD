package com.example.ai.airepair.constant;

import java.util.List;

/**
 * Auth模块导入常量
 */
public class AuthLayerImportConstants {
    
    /**
     * Auth Repository层常用导入白名单
     */
    public static final List<String> AUTH_REPOSITORY_IMPORT_WHITELIST = List.of(
        "import java.util.List;",
        "import java.util.Optional;"
    );
    
    /**
     * Auth Service层常用导入白名单
     */
    public static final List<String> AUTH_SERVICE_IMPORT_WHITELIST = List.of(
        "import java.util.List;",
        "import java.util.Optional;",
        "import java.util.HashMap;",
        "import java.util.Map;",
        "import java.time.LocalDateTime;",
        "import org.springframework.stereotype.Service;",
        "import org.springframework.beans.factory.annotation.Autowired;",
        "import org.springframework.http.HttpStatus;",
        "import org.springframework.web.server.ResponseStatusException;",
        "import com.example.demo.auth.repository.AuthRepository;",
        "import com.example.demo.auth.util.JwtUtil;",
        "import com.example.demo.entity.User;"
    );
    
    /**
     * Auth Controller层常用导入白名单
     */
    public static final List<String> AUTH_CONTROLLER_IMPORT_WHITELIST = List.of(
        "import java.util.Map;",
        "import org.springframework.beans.factory.annotation.Autowired;",
        "import org.springframework.http.ResponseEntity;",
        "import org.springframework.web.bind.annotation.*;",
        "import org.springframework.web.bind.annotation.RequestBody;",
        "import org.springframework.web.bind.annotation.RequestHeader;",
        "import org.springframework.http.HttpStatus;",
        "import com.example.demo.auth.service.AuthService;",
        "import com.example.demo.auth.util.JwtUtil;"
    );
    
    /**
     * Auth Request类常用导入白名单
     */
    public static final List<String> AUTH_REQUEST_IMPORT_WHITELIST = List.of(
        "import lombok.Data;"
    );
    
    /**
     * Auth Response类常用导入白名单
     */
    public static final List<String> AUTH_RESPONSE_IMPORT_WHITELIST = List.of(
        "import lombok.Data;",
        "import java.time.LocalDateTime;"
    );
}