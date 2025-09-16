package com.example.ai.flowise.agent.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequirementDocumentResponse {
    @JsonProperty("json")
    private DocumentContent json;
    
    @Data
    public static class DocumentContent {
        private String name;
        private String file;
    }
}