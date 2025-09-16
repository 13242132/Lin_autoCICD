package com.example.ai.flowise.memory.service;

import com.example.ai.flowise.memory.dto.SaveMemoryRequest;
import com.example.ai.flowise.memory.dto.QueryMemoryRequest;
import com.example.ai.flowise.memory.dto.ChatMemoryResponse;
import java.util.List;

/**
 * 记忆管理服务接口
 */
public interface MemoryService {
    
    /**
     * 保存聊天记忆
     * @param request 保存聊天记忆请求
     * @return 保存是否成功
     */
    boolean saveChatMemory(SaveMemoryRequest request);
    
    /**
     * 批量保存聊天记忆
     * @param requests 保存聊天记忆请求列表
     * @return 保存是否成功
     */
    boolean saveBatchChatMemory(List<SaveMemoryRequest> requests);
    
    /**
     * 查询聊天记忆
     * @param request 查询聊天记忆请求
     * @return 聊天记忆响应列表
     */
    List<ChatMemoryResponse> queryChatMemory(QueryMemoryRequest request);
    
    /**
     * 获取项目最近聊天记录
     * @param projectId 项目ID
     * @param limit 查询数量限制
     * @return 聊天记忆响应列表
     */
    List<ChatMemoryResponse> getRecentChatHistory(String projectId, int limit);
    
    /**
     * 获取项目聊天记录
     * @param projectId 项目ID
     * @param sessionId 会话ID
     * @param limit 查询数量限制
     * @return 聊天记忆响应列表
     */
    List<ChatMemoryResponse> getRecentSessionHistory(String projectId, String sessionId, int limit);
    
    /**
     * 删除项目聊天记忆
     * @param projectId 项目ID
     * @return 删除是否成功
     */
    boolean deleteProjectMemory(String projectId);
    
    /**
     * 清理过期记忆
     * @return 清理是否成功
     */
    boolean cleanExpiredMemory();
}