package com.example.ai.orchestration.entity;

/**
 * 文件实体类
 * 用于表示项目中的文件信息
 */
public class File {
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 文件内容
     */
    private String content;
    
    /**
     * 文件类型
     */
    private String type;
    
    public File() {
    }
    
    public File(String fileName, String content, String type) {
        this.fileName = fileName;
        this.content = content;
        this.type = type;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return "File{" +
                "fileName='" + fileName + '\'' +
                ", content='" + content + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}