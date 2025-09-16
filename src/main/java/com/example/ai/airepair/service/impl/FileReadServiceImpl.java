package com.example.ai.airepair.service.impl;

import com.example.ai.airepair.dto.FileReadRequest;
import com.example.ai.airepair.dto.FileReadResponse;
import com.example.ai.airepair.dto.FileOperationRequest;
import com.example.ai.airepair.dto.FileOperationResponse;
import com.example.ai.airepair.service.FileReadService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileReadServiceImpl implements FileReadService {

    @Override
    public FileReadResponse readFiles(FileReadRequest request) throws Exception {
        FileReadResponse response = new FileReadResponse();
        List<FileReadResponse.FileContent> files = new ArrayList<>();
        long totalSize = 0;
        
        try {
            Path startPath = Paths.get(request.getFilePath());
            
            if (!Files.exists(startPath)) {
                response.setSuccess(false);
                response.setMessage("指定的路径不存在: " + request.getFilePath());
                return response;
            }
            
            if (Files.isDirectory(startPath)) {
                // 处理目录
                try (Stream<Path> paths = request.isIncludeSubdirectories() ? 
                        Files.walk(startPath) : Files.list(startPath)) {
                    
                    List<Path> filePaths = paths
                            .filter(Files::isRegularFile)
                            .filter(path -> isFileExtensionMatch(path, request.getFileExtensions()))
                            .filter(path -> isFileSizeWithinLimit(path, request.getMaxFileSizeKB()))
                            .collect(Collectors.toList());
                    
                    for (Path filePath : filePaths) {
                        FileReadResponse.FileContent fileContent = readFileContent(filePath);
                        files.add(fileContent);
                        totalSize += fileContent.getFileSizeKB();
                    }
                }
            } else {
                // 处理单个文件
                if (isFileExtensionMatch(startPath, request.getFileExtensions()) && 
                    isFileSizeWithinLimit(startPath, request.getMaxFileSizeKB())) {
                    FileReadResponse.FileContent fileContent = readFileContent(startPath);
                    files.add(fileContent);
                    totalSize += fileContent.getFileSizeKB();
                }
            }
            
            response.setSuccess(true);
            response.setMessage("成功读取 " + files.size() + " 个文件");
            response.setFiles(files);
            response.setTotalFilesRead(files.size());
            response.setTotalSizeKB(totalSize);
            
        } catch (IOException e) {
            response.setSuccess(false);
            response.setMessage("读取文件时发生错误: " + e.getMessage());
            throw new Exception("读取文件时发生错误", e);
        }
        
        return response;
    }

    @Override
    public String readSingleFile(String filePath) throws Exception {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path) || !Files.isRegularFile(path)) {
                throw new Exception("文件不存在或不是常规文件: " + filePath);
            }
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new Exception("读取文件时发生错误: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean fileExists(String filePath) {
        Path path = Paths.get(filePath);
        return Files.exists(path) && Files.isRegularFile(path);
    }
    
    @Override
    public String listAllFiles(String path, boolean includeSubdirectories) throws Exception {
        try {
            Path startPath = Paths.get(path);
            
            if (!Files.exists(startPath)) {
                throw new Exception("指定的路径不存在: " + path);
            }
            
            StringBuilder result = new StringBuilder();
            
            if (Files.isDirectory(startPath)) {
                // 处理目录
                try (Stream<Path> paths = includeSubdirectories ? 
                        Files.walk(startPath) : Files.list(startPath)) {
                    
                    List<Path> allPaths = paths.collect(Collectors.toList());
                    
                    for (Path filePath : allPaths) {
                        BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
                        String type = attrs.isDirectory() ? "[目录]" : "[文件]";
                        String size = attrs.isDirectory() ? "" : " (" + (Files.size(filePath) / 1024) + "KB)";
                        result.append(type).append(" ").append(filePath.toString()).append(size).append("\n");
                    }
                }
            } else {
                // 处理单个文件
                BasicFileAttributes attrs = Files.readAttributes(startPath, BasicFileAttributes.class);
                String type = attrs.isDirectory() ? "[目录]" : "[文件]";
                String size = attrs.isDirectory() ? "" : " (" + (Files.size(startPath) / 1024) + "KB)";
                result.append(type).append(" ").append(startPath.toString()).append(size).append("\n");
            }
            
            return result.toString();
        } catch (IOException e) {
            throw new Exception("列出文件时发生错误: " + e.getMessage(), e);
        }
    }
    
    private boolean isFileExtensionMatch(Path path, List<String> extensions) {
        if (extensions == null || extensions.isEmpty()) {
            return true;
        }
        
        String fileName = path.getFileName().toString();
        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        
        return extensions.stream()
                .map(String::toLowerCase)
                .anyMatch(ext -> ext.equals(fileExtension));
    }
    
    private boolean isFileSizeWithinLimit(Path path, int maxSizeKB) {
        if (maxSizeKB <= 0) {
            return true;
        }
        
        try {
            long fileSizeBytes = Files.size(path);
            long fileSizeKB = fileSizeBytes / 1024;
            return fileSizeKB <= maxSizeKB;
        } catch (IOException e) {
            return false;
        }
    }
    
    private FileReadResponse.FileContent readFileContent(Path filePath) throws IOException {
        FileReadResponse.FileContent fileContent = new FileReadResponse.FileContent();
        
        String fileName = filePath.getFileName().toString();
        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
        long fileSizeBytes = Files.size(filePath);
        long fileSizeKB = fileSizeBytes / 1024;
        
        fileContent.setFileName(fileName);
        fileContent.setFilePath(filePath.toString());
        fileContent.setContent(new String(Files.readAllBytes(filePath)));
        fileContent.setFileSizeKB(fileSizeKB);
        fileContent.setFileExtension(fileExtension);
        
        return fileContent;
     }
     
     @Override
     public FileOperationResponse executeFileOperation(FileOperationRequest request) throws Exception {
        String operationType = request.getOperationType();
        FileOperationResponse response = new FileOperationResponse();
        
        // 设置操作类型
        response.setOperationType(operationType);
        
        try {
            switch (operationType) {
                case "READ_FILE":
                    return handleReadFileOperation(request);
                case "LIST_DIRECTORY":
                    return handleListDirectoryOperation(request);
                default:
                    response.setSuccess(false);
                    response.setMessage("不支持的操作类型: " + operationType);
                    return response;
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("执行操作失败: " + e.getMessage());
            return response;
        }
    }
    
    private FileOperationResponse handleReadFileOperation(FileOperationRequest request) throws Exception {
        FileOperationResponse response = new FileOperationResponse();
        String filePath = request.getFilePath();
        
        if (filePath == null || filePath.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("文件路径不能为空");
            return response;
        }
        
        // 检查文件是否存在
        if (!fileExists(filePath)) {
            response.setSuccess(false);
            response.setMessage("文件不存在: " + filePath);
            return response;
        }
        
        // 读取文件内容
        String content = readSingleFile(filePath);
        
        // 构建响应数据
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("filePath", filePath);
        
        FileOperationResponse.FileContent fileContent = new FileOperationResponse.FileContent();
        fileContent.setFileName(Paths.get(filePath).getFileName().toString());
        fileContent.setFilePath(filePath);
        fileContent.setContent(content);
        
        // 获取文件大小
        try {
            Path path = Paths.get(filePath);
            long fileSize = Files.size(path);
            fileContent.setFileSizeKB(fileSize / 1024.0);
            
            // 获取文件扩展名
            String fileName = Paths.get(filePath).getFileName().toString();
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0) {
                fileContent.setFileExtension(fileName.substring(dotIndex + 1));
            }
        } catch (IOException e) {
            // 忽略文件大小和扩展名获取错误
        }
        
        response.setSuccess(true);
        response.setMessage("文件读取成功");
        response.setParameters(parameters);
        response.setData(fileContent);
        
        return response;
    }
    
    private FileOperationResponse handleListDirectoryOperation(FileOperationRequest request) throws Exception {
        FileOperationResponse response = new FileOperationResponse();
        String directoryPath = request.getDirectoryPath();
        Boolean includeSubdirectories = request.getIncludeSubdirectories();
        
        if (directoryPath == null || directoryPath.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("目录路径不能为空");
            return response;
        }
        
        // 检查目录是否存在
        Path path = Paths.get(directoryPath);
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            response.setSuccess(false);
            response.setMessage("目录不存在: " + directoryPath);
            return response;
        }
        
        // 列出目录内容
        String listResult = listAllFiles(directoryPath, includeSubdirectories != null ? includeSubdirectories : false);
        
        // 解析listAllFiles的结果，构建目录内容
        FileOperationResponse.DirectoryContent directoryContent = new FileOperationResponse.DirectoryContent();
        directoryContent.setDirectoryPath(directoryPath);
        directoryContent.setIncludeSubdirectories(includeSubdirectories != null ? includeSubdirectories : false);
        
        List<FileOperationResponse.DirectoryItem> items = new ArrayList<>();
        
        // 解析listAllFiles的结果
        String[] lines = listResult.split("\n");
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            // 解析格式: [类型] 路径 (大小)
            FileOperationResponse.DirectoryItem item = new FileOperationResponse.DirectoryItem();
            
            if (line.startsWith("[目录]")) {
                item.setType("directory");
                item.setPath(line.substring("[目录]".length()).trim());
            } else if (line.startsWith("[文件]")) {
                item.setType("file");
                String filePart = line.substring("[文件]".length()).trim();
                
                // 提取文件路径和大小
                int sizeIndex = filePart.lastIndexOf("(");
                if (sizeIndex > 0) {
                    item.setPath(filePart.substring(0, sizeIndex).trim());
                    String sizeStr = filePart.substring(sizeIndex + 1, filePart.length() - 1);
                    try {
                        item.setSize(Double.parseDouble(sizeStr.replace("KB", "").trim()));
                    } catch (NumberFormatException e) {
                        // 忽略大小解析错误
                    }
                } else {
                    item.setPath(filePart);
                }
            }
            
            items.add(item);
        }
        
        directoryContent.setItems(items);
        
        // 构建响应参数
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("directoryPath", directoryPath);
        parameters.put("includeSubdirectories", includeSubdirectories != null ? includeSubdirectories : false);
        
        response.setSuccess(true);
        response.setMessage("目录列表获取成功");
        response.setParameters(parameters);
        response.setData(directoryContent);
        
        return response;
    }
}