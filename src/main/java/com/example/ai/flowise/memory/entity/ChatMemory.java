package com.example.ai.flowise.memory.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 聊天记忆实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMemory {
    
    /**
     * 记录ID
     */
    private String id;
    
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
     * 时间戳
     */
    private LocalDateTime timestamp;
    
    /**
     * 记录类型（例如：CHAT、CODE_GENERATION、REPAIR等）
     */
    private String type;
}