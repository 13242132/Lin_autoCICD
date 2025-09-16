package com.example.ai.flowise.agent.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 原型生成响应类
 * 用于解析Flowise原型生成工作流返回的JSON响应
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrototypeResponse {
    
    @JsonProperty("text")
    private String text;
    
    @JsonProperty("json")
    private PrototypeData json;
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PrototypeData {
        @JsonProperty("files")
        private List<PrototypeFile> files;
    }
    
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PrototypeFile {
        @JsonProperty("file_name")
        private String fileName;
        
        @JsonProperty("content")
        private String content;
    }
}