package com.example.ai.airepair.dto;

import java.util.List;
import java.util.Map;

public class FileOperationResponse {
    
    private boolean success;
    private String message;
    private String operationType;
    private Object result;
    private Map<String, Object> metadata;
    private Map<String, Object> parameters;
    private Object data;
    
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
    
    public String getOperationType() {
        return operationType;
    }
    
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
    
    public Object getResult() {
        return result;
    }
    
    public void setResult(Object result) {
        this.result = result;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    /**
     * 文件内容信息类
     */
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
        
        public void setFileSizeKB(double fileSizeKB) {
            this.fileSizeKB = (long) fileSizeKB;
        }
        
        public String getFileExtension() {
            return fileExtension;
        }
        
        public void setFileExtension(String fileExtension) {
            this.fileExtension = fileExtension;
        }
    }
    
    /**
     * 目录项信息类
     */
    public static class DirectoryItem {
        private String name;
        private String path;
        private boolean isDirectory;
        private long sizeKB;
        private String lastModified;
        private String type;
        private double size;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getPath() {
            return path;
        }
        
        public void setPath(String path) {
            this.path = path;
        }
        
        public boolean isDirectory() {
            return isDirectory;
        }
        
        public void setDirectory(boolean directory) {
            isDirectory = directory;
        }
        
        public long getSizeKB() {
            return sizeKB;
        }
        
        public void setSizeKB(long sizeKB) {
            this.sizeKB = sizeKB;
        }
        
        public String getLastModified() {
            return lastModified;
        }
        
        public void setLastModified(String lastModified) {
            this.lastModified = lastModified;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public double getSize() {
            return size;
        }
        
        public void setSize(double size) {
            this.size = size;
        }
    }
    
    /**
     * 目录内容信息类
     */
    public static class DirectoryContent {
        private String directoryPath;
        private boolean includeSubdirectories;
        private List<DirectoryItem> items;
        
        public String getDirectoryPath() {
            return directoryPath;
        }
        
        public void setDirectoryPath(String directoryPath) {
            this.directoryPath = directoryPath;
        }
        
        public boolean isIncludeSubdirectories() {
            return includeSubdirectories;
        }
        
        public void setIncludeSubdirectories(boolean includeSubdirectories) {
            this.includeSubdirectories = includeSubdirectories;
        }
        
        public List<DirectoryItem> getItems() {
            return items;
        }
        
        public void setItems(List<DirectoryItem> items) {
            this.items = items;
        }
    }
}