package com.example.ai.codegeneration.service;

import java.nio.file.Path;

/**
 * 异常处理类生成服务接口
 */
public interface ExceptionGenerationService {
    
    /**
     * 生成异常处理相关类，包括BusinessException、ErrorResponse和GlobalExceptionHandler
     * @param projectDir 项目目录
     * @return 是否生成成功
     */
    boolean generateExceptionClasses(Path projectDir);
}