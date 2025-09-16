package com.example.ai.airepair.dto;

import java.util.List;

public class FileReadRequest {
    
    private String filePath;
    private List<String> fileExtensions;
    private boolean includeSubdirectories;
    private int maxFileSizeKB;
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public List<String> getFileExtensions() {
        return fileExtensions;
    }
    
    public void setFileExtensions(List<String> fileExtensions) {
        this.fileExtensions = fileExtensions;
    }
    
    public boolean isIncludeSubdirectories() {
        return includeSubdirectories;
    }
    
    public void setIncludeSubdirectories(boolean includeSubdirectories) {
        this.includeSubdirectories = includeSubdirectories;
    }
    
    public int getMaxFileSizeKB() {
        return maxFileSizeKB;
    }
    
    public void setMaxFileSizeKB(int maxFileSizeKB) {
        this.maxFileSizeKB = maxFileSizeKB;
    }
}