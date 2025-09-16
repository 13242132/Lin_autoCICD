package com.example.ai.airepair.dto;

import java.util.List;

public class FileReadResponse {
    
    private boolean success;
    private String message;
    private List<FileContent> files;
    private int totalFilesRead;
    private long totalSizeKB;
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public List<FileContent> getFiles() {
        return files;
    }
    
    public void setFiles(List<FileContent> files) {
        this.files = files;
    }
    
    public int getTotalFilesRead() {
        return totalFilesRead;
    }
    
    public void setTotalFilesRead(int totalFilesRead) {
        this.totalFilesRead = totalFilesRead;
    }
    
    public long getTotalSizeKB() {
        return totalSizeKB;
    }
    
    public void setTotalSizeKB(long totalSizeKB) {
        this.totalSizeKB = totalSizeKB;
    }
    
    public static class FileContent {
        private String fileName;
        private String filePath;
        private String content;
        private long fileSizeKB;
        private String fileExtension;
        
        public String getFileName() {
            return fileName;
        }
        
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
        
        public String getFilePath() {
            return filePath;
        }
        
        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
        
        public long getFileSizeKB() {
            return fileSizeKB;
        }
        
        public void setFileSizeKB(long fileSizeKB) {
            this.fileSizeKB = fileSizeKB;
        }
        
        public String getFileExtension() {
            return fileExtension;
        }
        
        public void setFileExtension(String fileExtension) {
            this.fileExtension = fileExtension;
        }
    }
}