package com.example.ai.airepair.dto;

import java.util.Map;

public class FileOperationRequest {
    
    /**
     * 操作类型：READ_FILE 或 LIST_DIRECTORY
     */
    private String operationType;
    
    /**
     * 操作参数，动态构建
     */
    private Map<String, Object> parameters;
    
    public String getOperationType() {
        return operationType;
    }
    
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
    
    /**
     * 获取文件路径
     */
    public String getFilePath() {
        return parameters != null ? (String) parameters.get("filePath") : null;
    }
    
    /**
     * 获取目录路径
     */
    public String getDirectoryPath() {
        return parameters != null ? (String) parameters.get("directoryPath") : null;
    }
    
    /**
     * 获取最大深度
     */
    public Integer getMaxDepth() {
        return parameters != null ? (Integer) parameters.get("maxDepth") : null;
    }
    
    /**
     * 获取是否包含子目录
     */
    public Boolean getIncludeSubdirectories() {
        return parameters != null ? (Boolean) parameters.get("includeSubdirectories") : null;
    }
    
    /**
     * 获取文件扩展名过滤
     */
    @SuppressWarnings("unchecked")
    public java.util.List<String> getFileExtensions() {
        return parameters != null ? (java.util.List<String>) parameters.get("fileExtensions") : null;
    }
    
    /**
     * 获取最大文件大小（KB）
     */
    public Integer getMaxFileSizeKB() {
        return parameters != null ? (Integer) parameters.get("maxFileSizeKB") : null;
    }
}