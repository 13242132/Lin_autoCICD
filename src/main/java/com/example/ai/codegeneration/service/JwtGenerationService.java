package com.example.ai.codegeneration.service;

import java.nio.file.Path;

public interface JwtGenerationService {
    /**
     * 生成JWT相关的三个类：JwtUtil、JwtInterceptor、WebConfig
     * @param targetDir 目标目录路径
     * @return 是否生成成功
     */
    boolean generateJwtClasses(Path targetDir);
}