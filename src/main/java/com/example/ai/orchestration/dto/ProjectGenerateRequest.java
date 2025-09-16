package com.example.ai.orchestration.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class ProjectGenerateRequest {

    private String projectName;
    private String projectDescription;
    private String databaseType;
    private List<FileParameter> files;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public List<FileParameter> getFiles() {
        return files;
    }

    public void setFiles(List<FileParameter> files) {
        this.files = files;
    }

    public void setFilesFromString(String filesJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            this.files = objectMapper.readValue(filesJson, new TypeReference<List<FileParameter>>() {});
        } catch (IOException e) {
            // For simplicity in this context, we'll wrap the checked exception.
            // In a real application, a custom exception might be better.
            throw new RuntimeException("Error parsing files JSON", e);
        }
    }

    public String getFilesContentAsString() {
        if (this.files == null || this.files.isEmpty()) {
            return "";
        }

        StringBuilder filesContentBuilder = new StringBuilder();
        for (FileParameter file : this.files) {
            filesContentBuilder.append("File: `").append(file.getFileName()).append("`\n");
            filesContentBuilder.append("```\n");
            filesContentBuilder.append(file.getContent());
            filesContentBuilder.append("\n```\n\n");
        }
        return filesContentBuilder.toString();
    }

    public static class FileParameter {
        private String fileName;
        private String content;
        private String type;

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
    }
}
