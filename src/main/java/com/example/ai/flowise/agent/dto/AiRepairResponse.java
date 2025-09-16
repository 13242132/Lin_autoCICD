package com.example.ai.flowise.agent.dto;

import lombok.Data;

/**
 * AI修复响应DTO
 */
@Data
public class AiRepairResponse {

    /**
     * 根路径
     */
    private String rootPath;

    /**
     * 目标文件路径
     */
    private String targetFilePath;

    /**
     * 工具名称
     */
    private String toolName;

    /**
     * 解释部分
     * 对操作结果的简要解释
     */
    private String explanation;

    /**
     * 操作内容
     * 例如写入文件的内容或读取的文件内容
     */
    private String operationContent;
}