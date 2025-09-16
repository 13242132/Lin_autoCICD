package com.example.ai.codegeneration.service;

import com.example.ai.flowise.agent.EntityGeneratorService;
import java.nio.file.Path;

/**
 * 实体处理服务接口，负责处理AI生成实体的相关逻辑
 */
public interface EntityProcessingService {
    
    /**
     * 处理实体生成和文件写入
     * 
     * @param entityList 实体列表字符串
     * @param entityGeneratorService 实体生成服务
     * @param tempDir 临时目录
     * @return 完整的实体内容，用于后续步骤
     * @throws Exception 处理过程中可能出现的异常
     */
    String processEntities(String entityList, EntityGeneratorService entityGeneratorService, Path tempDir) throws Exception;
    
    /**
     * 为实体类生成对应的CRUD接口
     * 
     * @param entityName 实体类名称
     * @param tempDir 临时目录
     * @return 是否生成成功
     * @throws Exception 处理过程中可能出现的异常
     */
    boolean generateCrudInterfaces(String entityName, Path tempDir) throws Exception;
}