package com.example.ai.flowise.agent.dto;

import lombok.Data;

/**
 * AI修复请求DTO
 */
@Data
public class AiRepairRequest {

    /**
     * 请求内容
     * 包含工具调用信息或修复任务的描述
     */
    private String content;
}