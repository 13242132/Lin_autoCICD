package com.example.ai.flowise.agent.util;

import com.example.ai.orchestration.entity.File;
import com.example.ai.flowise.agent.dto.RequirementDocumentResponse;
import com.example.ai.flowise.agent.dto.PrototypeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

/**
 * 响应转换工具类
 * 将Flowise API响应转换为File列表
 */
@Component
@Slf4j
public class ResponseConverter {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 将需求文档响应转换为File列表
     * @param response 需求文档响应
     * @return File列表
     */
    public List<File> convertRequirementDocumentToFiles(RequirementDocumentResponse response) {
        List<File> files = new ArrayList<>();
        
        if (response != null && response.getJson() != null) {
            String fileName = response.getJson().getName();
            String content = response.getJson().getFile();
            
            if (fileName != null && content != null) {
                File file = new File();
                file.setFileName(fileName);
                file.setContent(content);
                file.setType("requirement_document");
                files.add(file);
                
                log.info("成功转换需求文档为File对象: {}", fileName);
            }
        }
        
        return files;
    }
    
    /**
     * 将原型响应转换为File列表
     * @param response 原型响应
     * @return File列表
     */
    public List<File> convertPrototypeToFiles(PrototypeResponse response) {
        List<File> files = new ArrayList<>();
        
        if (response != null) {
            // 首先尝试从json字段获取文件列表
            if (response.getJson() != null && response.getJson().getFiles() != null) {
                for (PrototypeResponse.PrototypeFile prototypeFile : response.getJson().getFiles()) {
                    if (prototypeFile.getFileName() != null && prototypeFile.getContent() != null) {
                        File file = new File();
                        file.setFileName(prototypeFile.getFileName());
                        file.setContent(prototypeFile.getContent());
                        file.setType("prototype");
                        files.add(file);
                        
                        log.info("成功转换原型文件为File对象: {}", prototypeFile.getFileName());
                    }
                }
            }
            // 如果json字段为空，尝试从text字段解析
            else if (response.getText() != null && !response.getText().trim().isEmpty()) {
                try {
                    String textContent = response.getText();
                    log.info("开始解析text字段中的JSON数据，长度: {}", textContent.length());
                    log.debug("text字段内容前500字符: {}", textContent.substring(0, Math.min(500, textContent.length())));
                    
                    // 清理text内容，移除markdown标记
                    String cleanedText = textContent
                        .replaceAll("```json\\s*", "")  // 移除```json标记
                        .replaceAll("```\\s*", "")      // 移除```标记
                        .trim();
                    
                    // 如果内容包含多个JSON对象（通过}\n{模式识别），需要分割处理
                    if (cleanedText.contains("}\n{")) {
                        // 按照}\n{模式分割多个JSON对象
                        String[] jsonObjects = cleanedText.split("\\}\\s*\\n\\s*\\{");
                        for (int i = 0; i < jsonObjects.length; i++) {
                            String jsonStr = jsonObjects[i].trim();
                            // 为分割后的片段添加缺失的大括号
                            if (i == 0 && !jsonStr.endsWith("}")) {
                                jsonStr += "}";
                            } else if (i == jsonObjects.length - 1 && !jsonStr.startsWith("{")) {
                                jsonStr = "{" + jsonStr;
                            } else if (i > 0 && i < jsonObjects.length - 1) {
                                if (!jsonStr.startsWith("{")) jsonStr = "{" + jsonStr;
                                if (!jsonStr.endsWith("}")) jsonStr += "}";
                            }
                            
                            try {
                                JsonNode fileNode = objectMapper.readTree(jsonStr);
                                if (fileNode.has("file_name") && fileNode.has("content")) {
                                    String fileName = fileNode.get("file_name").asText();
                                    String content = fileNode.get("content").asText();
                                    
                                    if (fileName != null && !fileName.isEmpty() && content != null) {
                                        File file = new File();
                                        file.setFileName(fileName);
                                        file.setContent(content);
                                        file.setType("prototype");
                                        files.add(file);
                                        
                                        log.info("成功从text字段解析原型文件: {}", fileName);
                                    }
                                }
                            } catch (Exception ex) {
                                log.warn("解析分割后的JSON对象失败: {}, JSON内容: {}", ex.getMessage(), jsonStr.substring(0, Math.min(100, jsonStr.length())));
                            }
                        }
                        
                        // 如果通过分割成功解析了文件，直接返回
                        if (!files.isEmpty()) {
                            log.info("原型响应转换完成，共转换 {} 个文件", files.size());
                            return files;
                        }
                    }
                    
                    log.debug("清理后的text内容前2000字符: {}", cleanedText.substring(0, Math.min(2000, cleanedText.length())));
                    log.debug("清理后的text内容后2000字符: {}", cleanedText.substring(Math.max(0, cleanedText.length() - 2000)));
                    
                    // 统计大括号数量，帮助调试
                    long openBraces = cleanedText.chars().filter(ch -> ch == '{').count();
                    long closeBraces = cleanedText.chars().filter(ch -> ch == '}').count();
                    log.debug("text内容中包含 {} 个开括号，{} 个闭括号", openBraces, closeBraces);
                    
                    // 检查是否包含多个文件（通过查找多个file_name字段）
                    int fileNameCount = (cleanedText.split("\"file_name\"").length - 1);
                    log.debug("text内容中包含 {} 个file_name字段", fileNameCount);
                    
                    // 首先尝试将整个text内容作为JSON解析
                    try {
                        JsonNode rootNode = objectMapper.readTree(cleanedText);
                        
                        // 检查是否有files数组
                        if (rootNode.has("files") && rootNode.get("files").isArray()) {
                            for (JsonNode fileNode : rootNode.get("files")) {
                                if (fileNode.has("file_name") && fileNode.has("content")) {
                                    String fileName = fileNode.get("file_name").asText();
                                    String content = fileNode.get("content").asText();
                                    
                                    if (fileName != null && !fileName.isEmpty() && content != null) {
                                        File file = new File();
                                        file.setFileName(fileName);
                                        file.setContent(content);
                                        file.setType("prototype");
                                        files.add(file);
                                        
                                        log.info("成功从text字段解析原型文件: {}", fileName);
                                    }
                                }
                            }
                        }
                        // 如果没有files数组，检查是否直接包含file_name和content
                        else if (rootNode.has("file_name") && rootNode.has("content")) {
                            String fileName = rootNode.get("file_name").asText();
                            String content = rootNode.get("content").asText();
                            
                            if (fileName != null && !fileName.isEmpty() && content != null) {
                                File file = new File();
                                file.setFileName(fileName);
                                file.setContent(content);
                                file.setType("prototype");
                                files.add(file);
                                
                                log.info("成功从text字段解析原型文件: {}", fileName);
                            }
                        }
                    } catch (IOException e) {
                        log.warn("无法将text字段作为完整JSON解析，尝试分段解析: {}", e.getMessage());
                        
                        // 尝试按行分割并解析每个JSON对象
                        String[] lines = cleanedText.split("\n");
                        StringBuilder currentJson = new StringBuilder();
                        int braceCount = 0;
                        
                        for (String line : lines) {
                            currentJson.append(line).append("\n");
                            
                            // 计算当前累积内容的大括号数量
                            for (char c : line.toCharArray()) {
                                if (c == '{') braceCount++;
                                else if (c == '}') braceCount--;
                            }
                            
                            // 如果大括号平衡，尝试解析为JSON
                            if (braceCount == 0 && currentJson.length() > 0) {
                                String jsonStr = currentJson.toString().trim();
                                if (jsonStr.startsWith("{") && jsonStr.endsWith("}")) {
                                    try {
                                        JsonNode fileNode = objectMapper.readTree(jsonStr);
                                        if (fileNode.has("file_name") && fileNode.has("content")) {
                                            String fileName = fileNode.get("file_name").asText();
                                            String content = fileNode.get("content").asText();
                                            
                                            if (fileName != null && !fileName.isEmpty() && content != null) {
                                                File file = new File();
                                                file.setFileName(fileName);
                                                file.setContent(content);
                                                file.setType("prototype");
                                                files.add(file);
                                                
                                                log.info("成功从text字段解析原型文件: {}", fileName);
                                            }
                                        }
                                    } catch (Exception ex) {
                                        log.warn("解析JSON对象失败: {}, JSON内容: {}", ex.getMessage(), jsonStr.substring(0, Math.min(200, jsonStr.length())));
                                    }
                                }
                                currentJson = new StringBuilder();
                            }
                        }
                        
                        // 如果还有未处理的内容，尝试最后一次解析
                        if (currentJson.length() > 0) {
                            String jsonStr = currentJson.toString().trim();
                            if (jsonStr.startsWith("{") && jsonStr.endsWith("}")) {
                                try {
                                    JsonNode fileNode = objectMapper.readTree(jsonStr);
                                    if (fileNode.has("file_name") && fileNode.has("content")) {
                                        String fileName = fileNode.get("file_name").asText();
                                        String content = fileNode.get("content").asText();
                                        
                                        if (fileName != null && !fileName.isEmpty() && content != null) {
                                            File file = new File();
                                            file.setFileName(fileName);
                                            file.setContent(content);
                                            file.setType("prototype");
                                            files.add(file);
                                            
                                            log.info("成功从text字段解析原型文件: {}", fileName);
                                        }
                                    }
                                } catch (Exception ex) {
                                    log.warn("解析最后JSON对象失败: {}", ex.getMessage());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("解析text字段失败", e);
                }
            }
        }
        
        log.info("原型响应转换完成，共转换 {} 个文件", files.size());
        return files;
    }
}