// 文件路径：src/main/java/com/example/ai/project/fileprocess/impl/AuthWorkflowResponseParser.java

package com.example.ai.orchestration.fileprocess.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.ai.orchestration.fileprocess.WorkflowResponseParser;
import com.example.ai.airepair.service.AiRepairService;
import com.example.ai.airepair.constant.AuthLayerImportConstants;
import com.example.ai.util.MarkdownCleaner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * 专门用于解析 Auth AI 输出的代码生成结果
 * 支持提取并写入：Controller, Service, Repository, Entity, Util, Interceptor, Config, Request, Response
 * 要求输入内容以 ### ---AUTH_AI_OUTPUT_START--- 开头
 */
@Component("authWorkflowResponseParser")
public class AuthWorkflowResponseParser implements WorkflowResponseParser {

    private static final Logger log = LoggerFactory.getLogger(AuthWorkflowResponseParser.class);
    
    @Autowired
    private AiRepairService aiRepairService;

    @Override
    public boolean parseAndWrite(String responseContent, Path targetDir, Map<String, Object> sharedContext) {
        if (responseContent == null || targetDir == null) {
            log.error("Auth response content or target directory cannot be null");
            return false;
        }

        // 清理整个回复内容中的Markdown格式标记
        String cleanedContent = MarkdownCleaner.clean(responseContent);
        
        // 标准化文本，去除所有标记前的转义字符
        String normalizedContent = cleanedContent.replaceAll("\\\\(### ---)", "$1");

        // 1. 验证是否以 AUTH_AI 标记开头（增强识别能力）
        if (!normalizedContent.trim().startsWith("### ---AUTH_AI_OUTPUT_START---")) {
            log.warn("Auth AI response does not start with expected marker. Attempting to parse anyway...");
        }

        try {
            // 2. 确保目标目录存在
            ensureDirectoryExists(targetDir.toString());

            // 3. 定义标准目录结构（与 CRUD 一致）
          
            Path repositoryDir = targetDir.resolve("src/main/java/com/example/demo/auth/repository");
            Path serviceDir = targetDir.resolve("src/main/java/com/example/demo/auth/service");
            Path controllerDir = targetDir.resolve("src/main/java/com/example/demo/auth/controller");
            Path requestDir = targetDir.resolve("src/main/java/com/example/demo/auth/request");
            Path responseDir = targetDir.resolve("src/main/java/com/example/demo/auth/response");

            // 创建所有目录
      
            Files.createDirectories(repositoryDir);
            Files.createDirectories(serviceDir);
            Files.createDirectories(controllerDir);
            Files.createDirectories(requestDir);
            Files.createDirectories(responseDir);
    

            log.debug("Created directories for auth modules under: {}", targetDir);

            // 4. 提取类名映射（用于文件命名）
            Map<String, String> classNames = extractClassNames(normalizedContent);

            // 5. 写入各个模块的代码
            writeIfContentExists(normalizedContent, "CONTROLLER", controllerDir, classNames.get("controller"), "Controller");
            writeIfContentExists(normalizedContent, "SERVICE", serviceDir, classNames.get("service"), "Service");
            writeIfContentExists(normalizedContent, "REPOSITORY", repositoryDir, classNames.get("repository"), "Repository");
            writeIfContentExists(normalizedContent, "REQUEST_CLASSES", requestDir, classNames.get("requestClasses"), "Requests");
            writeIfContentExists(normalizedContent, "RESPONSE_CLASSES", responseDir, classNames.get("responseClasses"), "Responses");

            // Entity（User）
            String entityName = classNames.get("entity");
            if (entityName == null || !"User".equalsIgnoreCase(entityName)) {    
            entityName = "User"; // 强制使用 User
            }
           
  
            log.info("Successfully parsed and wrote Auth AI response to project structure");
            return true;

        } catch (IOException e) {
            log.error("Failed to parse and write Auth AI response: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 判断内容是否为 null、空，或包含跳过标记（如 "// SKIPPED"）
     */
    private boolean isNullOrEmptyOrSkipped(String content) {
        if (content == null) {
            return true;
        }
        String trimmed = content.trim();
        // 空内容
        if (trimmed.isEmpty()) {
            return true;
        }
        // 常见跳过标记
        return trimmed.startsWith("// SKIPPED") ||
               trimmed.startsWith("/* SKIPPED") ||
               trimmed.endsWith("SKIPPED */") ||
               trimmed.contains("already exists") ||
               trimmed.contains("already generated") ||
               trimmed.equalsIgnoreCase("// NO ENTITY GENERATED");
    }
    /**
     * 提取类名映射：controller: AuthController -> map.put("controller", "AuthController")
     */
    private Map<String, String> extractClassNames(String text) {
        Map<String, String> names = new HashMap<>();
        String start = "### ---CLASS_NAMES_START---";
        String end = "### ---CLASS_NAMES_END---";

        int s = text.indexOf(start);
        int e = text.indexOf(end);
        if (s == -1 || e == -1) {
            log.warn("CLASS_NAMES block not found in Auth AI response");
            return names;
        }

        String block = text.substring(s + start.length(), e).trim();
        // 清理类名块中的Markdown格式标记
        String cleanedBlock = MarkdownCleaner.clean(block);
        for (String line : cleanedBlock.split("\n")) {
            line = line.trim();
            if (line.contains(":")) {
                String[] kv = line.split(":", 2);
                String key = kv[0].trim();
                String value = kv[1].trim();
                names.put(key, value);
            }
        }
        return names;
    }

    /**
     * 通用方法：提取指定部分并写入文件
     */
    private void writeIfContentExists(String text, String sectionName, Path dir, String className, String suffixIfBlank) {
        String content = extractSection(text, sectionName);
        if (content == null || content.trim().isEmpty()) {
            log.debug("No content found for section: {}", sectionName);
            return;
        }

        // 构建文件名
        String fileName = className;
        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = "Unknown" + (suffixIfBlank.isEmpty() ? "" : suffixIfBlank);
            log.warn("Using default file name for section {}: {}", sectionName, fileName);
        }
        // 清理非法字符
        fileName = fileName.replaceAll("[^a-zA-Z0-9_]", "_") + ".java";
        Path file = dir.resolve(fileName);

        try {
            // 写入前直接进行内容修复
            // 根据不同的sectionName选择不同的白名单
            String fixedContent;
            if ("REQUEST_CLASSES".equals(sectionName)) {
                fixedContent = aiRepairService.fixImports(content.trim(), AuthLayerImportConstants.AUTH_REQUEST_IMPORT_WHITELIST);
            } else if ("RESPONSE_CLASSES".equals(sectionName)) {
                fixedContent = aiRepairService.fixImports(content.trim(), AuthLayerImportConstants.AUTH_RESPONSE_IMPORT_WHITELIST);
            } else {
                fixedContent = aiRepairService.fixImports(content.trim(), AuthLayerImportConstants.AUTH_SERVICE_IMPORT_WHITELIST);
            }
            
            Files.writeString(file, fixedContent);
            log.debug("Wrote {} file: {}", sectionName, file);
        } catch (IOException e) {
            log.error("Failed to write {} file: {}", sectionName, file, e);
        }
    }

    /**
     * 提取指定标记之间的内容
     */
    private String extractSection(String text, String sectionName) {
        String startTag = "### ---" + sectionName + "_START---";
        String endTag = "### ---" + sectionName + "_END---";
        int start = text.indexOf(startTag);
        int end = text.indexOf(endTag);
        if (start == -1 || end == -1) {
            log.warn("Section not found: {}", sectionName);
            return null;
        }
        String extractedContent = text.substring(start + startTag.length(), end).trim();
        // 清理分块内容中的Markdown格式标记
        return MarkdownCleaner.clean(extractedContent);
    }

    /**
     * 确保目录存在
     */
    private void ensureDirectoryExists(String directoryPath) throws IOException {
        Path path = Path.of(directoryPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            log.debug("Created directory: {}", directoryPath);
        }
    }
}