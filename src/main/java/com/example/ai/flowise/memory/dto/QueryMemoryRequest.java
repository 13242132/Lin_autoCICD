package com.example.ai.flowise.memory.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 查询聊天记忆请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryMemoryRequest {
    
    /**
     * 项目ID
     */
    private String projectId;
    
    /**
     * 查询的记录数量，默认为10
     */
    private int limit = 10;
    
    /**
     * 记录类型（可选，用于过滤特定类型的记录）
     */
    private String type;
}