package com.example.ai.flowise.memory.controller;

import com.example.ai.flowise.memory.dto.SaveMemoryRequest;
import com.example.ai.flowise.memory.dto.QueryMemoryRequest;
import com.example.ai.flowise.memory.dto.ChatMemoryResponse;
import com.example.ai.flowise.memory.service.MemoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 记忆管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/memory")
public class MemoryController {

    @Autowired
    private MemoryService memoryService;

    /**
     * 保存聊天记忆
     */
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveChatMemory(@RequestBody SaveMemoryRequest request) {
        try {
            boolean success = memoryService.saveChatMemory(request);
            return ResponseEntity.ok(Map.of(
                "success", success,
                "message", success ? "聊天记忆保存成功" : "聊天记忆保存失败"
            ));
        } catch (Exception e) {
            log.error("保存聊天记忆异常", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "保存聊天记忆时发生异常: " + e.getMessage()
            ));
        }
    }

    /**
     * 批量保存聊天记忆
     */
    @PostMapping("/save-batch")
    public ResponseEntity<Map<String, Object>> saveBatchChatMemory(@RequestBody List<SaveMemoryRequest> requests) {
        try {
            boolean success = memoryService.saveBatchChatMemory(requests);
            return ResponseEntity.ok(Map.of(
                "success", success,
                "message", success ? "批量聊天记忆保存成功" : "批量聊天记忆保存失败"
            ));
        } catch (Exception e) {
            log.error("批量保存聊天记忆异常", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "批量保存聊天记忆时发生异常: " + e.getMessage()
            ));
        }
    }

    /**
     * 查询聊天记忆
     */
    @PostMapping("/query")
    public ResponseEntity<List<ChatMemoryResponse>> queryChatMemory(@RequestBody QueryMemoryRequest request) {
        try {
            List<ChatMemoryResponse> response = memoryService.queryChatMemory(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("查询聊天记忆异常", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取项目最近聊天记录
     */
    @GetMapping("/project/{projectId}/recent")
    public ResponseEntity<List<ChatMemoryResponse>> getRecentChatHistory(
            @PathVariable String projectId,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<ChatMemoryResponse> response = memoryService.getRecentChatHistory(projectId, limit);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取项目最近聊天记录异常", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取会话最近聊天记录
     */
    @GetMapping("/project/{projectId}/session/recent")
    public ResponseEntity<List<ChatMemoryResponse>> getRecentSessionHistory(
            @PathVariable String projectId,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String sessionId) {
        try {
            List<ChatMemoryResponse> response = memoryService.getRecentSessionHistory(projectId, sessionId, limit);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取会话最近聊天记录异常", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 删除项目聊天记忆
     */
    @DeleteMapping("/project/{projectId}")
    public ResponseEntity<Map<String, Object>> deleteProjectMemory(@PathVariable String projectId) {
        try {
            boolean success = memoryService.deleteProjectMemory(projectId);
            return ResponseEntity.ok(Map.of(
                "success", success,
                "message", success ? "项目聊天记忆删除成功" : "项目聊天记忆删除失败"
            ));
        } catch (Exception e) {
            log.error("删除项目聊天记忆异常", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "删除项目聊天记忆时发生异常: " + e.getMessage()
            ));
        }
    }

    /**
     * 清理过期记忆
     */
    @PostMapping("/clean-expired")
    public ResponseEntity<Map<String, Object>> cleanExpiredMemories() {
        try {
            boolean success = memoryService.cleanExpiredMemory();
            return ResponseEntity.ok(Map.of(
                "success", success,
                "message", success ? "清理过期记忆完成" : "清理过期记忆失败"
            ));
        } catch (Exception e) {
            log.error("清理过期记忆异常", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "清理过期记忆时发生异常: " + e.getMessage()
            ));
        }
    }
}