package com.example.ai.flowise.memory.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 * 聊天记忆响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMemoryResponse {
    
    /**
     * 项目ID
     */
    private String projectId;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 聊天记录列表
     */
    private List<ChatMemoryItem> memories;
    
    /**
     * 聊天记忆项
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMemoryItem {
        /**
         * 记录ID
         */
        private String id;
        
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
        private String timestamp;
        
        /**
         * 记录类型
         */
        private String type;
    }
}