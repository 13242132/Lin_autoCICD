package com.example.ai.flowise.memory.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 保存聊天记忆请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveMemoryRequest {
    
    /**
     * 项目ID
     */
    private String projectId;
    
    /**
     * 用户消息
     */
    private String userMessage;
    
    /**
     * AI回复
     */
    private String aiResponse;
    
    /**
     * 记录类型
     */
    private String type;
}