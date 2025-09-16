package com.example.ai.flowise.memory.service.impl;

import com.example.ai.flowise.memory.config.RedisConfig;
import com.example.ai.flowise.memory.dto.SaveMemoryRequest;
import com.example.ai.flowise.memory.dto.QueryMemoryRequest;
import com.example.ai.flowise.memory.dto.ChatMemoryResponse;
import com.example.ai.flowise.memory.entity.ChatMemory;
import com.example.ai.flowise.memory.service.MemoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 记忆管理服务实现类
 */
@Slf4j
@Service
public class MemoryServiceImpl implements MemoryService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private RedisConfig redisConfig;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private static final String PROJECT_MEMORY_KEY_PREFIX = "project:memory:";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public boolean saveChatMemory(SaveMemoryRequest request) {
        try {
            String memoryId = UUID.randomUUID().toString();
            ChatMemory chatMemory = new ChatMemory(
                memoryId,
                request.getProjectId(),
                request.getUserMessage(),
                request.getAiResponse(),
                LocalDateTime.now(),
                request.getType()
            );
            
            // 将对象转换为JSON字符串后再存储
            String jsonMemory = objectMapper.writeValueAsString(chatMemory);
            
            // 保存到项目记忆列表
            String projectKey = PROJECT_MEMORY_KEY_PREFIX + request.getProjectId();
            redisTemplate.opsForList().leftPush(projectKey, jsonMemory);
            
            // 限制项目记忆列表长度
            redisTemplate.opsForList().trim(projectKey, 0, redisConfig.getMaxChatHistory() - 1);
            
            // 设置过期时间
            redisTemplate.expire(projectKey, redisConfig.getTtlMinutes(), TimeUnit.MINUTES);
            
            return true;
        } catch (Exception e) {
            log.error("保存聊天记忆失败", e);
            return false;
        }
    }
    
    @Override
    public boolean saveBatchChatMemory(List<SaveMemoryRequest> requests) {
        try {
            if (requests == null || requests.isEmpty()) {
                return true;
            }
            
            // 获取项目ID，假设所有请求都是针对同一个项目
            String projectId = requests.get(0).getProjectId();
            String projectKey = PROJECT_MEMORY_KEY_PREFIX + projectId;
            
            // 批量转换并保存
            List<String> jsonMemories = new ArrayList<>();
            for (SaveMemoryRequest request : requests) {
                String memoryId = UUID.randomUUID().toString();
                ChatMemory chatMemory = new ChatMemory(
                    memoryId,
                    request.getProjectId(),
                    request.getUserMessage(),
                    request.getAiResponse(),
                    LocalDateTime.now(),
                    request.getType()
                );
                
                // 将对象转换为JSON字符串
                String jsonMemory = objectMapper.writeValueAsString(chatMemory);
                jsonMemories.add(jsonMemory);
            }
            
            // 批量保存到Redis
            redisTemplate.opsForList().leftPushAll(projectKey, jsonMemories);
            
            // 限制项目记忆列表长度
            redisTemplate.opsForList().trim(projectKey, 0, redisConfig.getMaxChatHistory() - 1);
            
            // 设置过期时间
            redisTemplate.expire(projectKey, redisConfig.getTtlMinutes(), TimeUnit.MINUTES);
            
            return true;
        } catch (Exception e) {
            log.error("批量保存聊天记忆失败", e);
            return false;
        }
    }
    
    @Override
    public List<ChatMemoryResponse> queryChatMemory(QueryMemoryRequest request) {
        try {
            String key = PROJECT_MEMORY_KEY_PREFIX + request.getProjectId();
            
            int limit = request.getLimit() > 0 ? request.getLimit() : 10;
            List<Object> memoryObjects = redisTemplate.opsForList().range(key, 0, limit - 1);
            
            List<ChatMemoryResponse.ChatMemoryItem> memories = (memoryObjects != null ? memoryObjects : Collections.emptyList())
                .stream()
                .map(obj -> {
                    try {
                        // 从JSON字符串反序列化为ChatMemory对象
                        return objectMapper.readValue(obj.toString(), ChatMemory.class);
                    } catch (Exception e) {
                        log.error("反序列化聊天记忆失败", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(memory -> request.getType() == null || request.getType().isEmpty() || 
                    (memory.getType() != null && memory.getType().equals(request.getType())))
                .map(memory -> new ChatMemoryResponse.ChatMemoryItem(
                    memory.getId(),
                    memory.getUserMessage(),
                    memory.getAiResponse(),
                    memory.getTimestamp().format(FORMATTER),
                    memory.getType()
                ))
                .collect(Collectors.toList());
            
            // 返回包含单个ChatMemoryResponse的列表
            ChatMemoryResponse response = new ChatMemoryResponse(request.getProjectId(), null, memories);
            return Collections.singletonList(response);
        } catch (Exception e) {
            log.error("查询聊天记忆失败", e);
            return Collections.singletonList(new ChatMemoryResponse(request.getProjectId(), null, Collections.emptyList()));
        }
    }
    
    @Override
    public List<ChatMemoryResponse> getRecentChatHistory(String projectId, int limit) {
        QueryMemoryRequest request = new QueryMemoryRequest();
        request.setProjectId(projectId);
        request.setLimit(limit);
        return queryChatMemory(request);
    }
    
    @Override
    public List<ChatMemoryResponse> getRecentSessionHistory(String projectId, String sessionId, int limit) {
        QueryMemoryRequest request = new QueryMemoryRequest();
        request.setProjectId(projectId);
        request.setLimit(limit);
        return queryChatMemory(request);
    }
    
    @Override
    public boolean deleteProjectMemory(String projectId) {
        try {
            // 删除项目记忆
            String projectKey = PROJECT_MEMORY_KEY_PREFIX + projectId;
            redisTemplate.delete(projectKey);
            
            return true;
        } catch (Exception e) {
            log.error("删除项目聊天记忆失败", e);
            return false;
        }
    }
    
    @Override
    public boolean cleanExpiredMemory() {
        // Redis会自动清理过期的键，这里可以添加额外的清理逻辑
        // 例如：清理特定类型的记忆或根据业务规则清理
        log.info("执行记忆清理任务");
        return true;
    }
}