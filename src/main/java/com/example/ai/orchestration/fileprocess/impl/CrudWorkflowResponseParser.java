package com.example.ai.orchestration.fileprocess.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.ai.orchestration.fileprocess.WorkflowResponseParser;
import com.example.ai.airepair.service.AiRepairService;
import com.example.ai.airepair.constant.CrudLayerImportConstants;
import com.example.ai.util.MarkdownCleaner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
@Component("crudWorkflowResponseParser")  // Bean 名必须唯一
public class CrudWorkflowResponseParser implements WorkflowResponseParser {

    private static final Logger log = LoggerFactory.getLogger(CrudWorkflowResponseParser.class);
    
    @Autowired
    private AiRepairService aiRepairService;

    @Override
    public boolean parseAndWrite(String responseContent, Path targetDir, Map<String, Object> sharedContext) {
        if (responseContent == null || targetDir == null) {
            log.error("Response content or target directory cannot be null");
            return false;
        }

        try {
            // 清理整个回复内容中的Markdown格式标记
            String cleanedContent = MarkdownCleaner.clean(responseContent);
            
            // 确保目标目录存在
            ensureDirectoryExists(targetDir.toString());

            // 定义标准目录结构
            Path repositoryDir = targetDir.resolve("src/main/java/com/example/demo/repository");
            Path serviceDir = targetDir.resolve("src/main/java/com/example/demo/service");
            Path controllerDir = targetDir.resolve("src/main/java/com/example/demo/controller");
            Path requestDir = targetDir.resolve("src/main/java/com/example/demo/request");
            Path responseDir = targetDir.resolve("src/main/java/com/example/demo/response");

            // 创建标准目录
            Files.createDirectories(repositoryDir);
            Files.createDirectories(serviceDir);
            Files.createDirectories(controllerDir);
            Files.createDirectories(requestDir);
            Files.createDirectories(responseDir);

            log.debug("Created standard directories under: {}", targetDir);

            // 提取类名映射
            Map<String, String> classNames = extractClassNames(cleanedContent);

            // 提取并写入仓库接口
            String repositoryContent = extractRepositoryFromResponse(cleanedContent);
            if (repositoryContent != null && !repositoryContent.trim().isEmpty()) {
                String repositoryName = classNames.get("repository");
                if (repositoryName != null) {
                    // 使用AI修复导入语句
                    String fixedRepositoryContent = aiRepairService.fixImports(repositoryContent.trim(), CrudLayerImportConstants.REPOSITORY_IMPORT_WHITELIST);
                    Path repositoryFile = repositoryDir.resolve(repositoryName + ".java");
                    Files.writeString(repositoryFile, fixedRepositoryContent);
                    log.debug("Wrote repository file: {}", repositoryFile);
                }
            }

            // 提取并写入服务类
            String serviceContent = extractServiceFromResponse(cleanedContent);
            if (serviceContent != null && !serviceContent.trim().isEmpty()) {
                String serviceName = classNames.get("service");
                if (serviceName != null) {
                    // 使用AI修复导入语句
                    String fixedServiceContent = aiRepairService.fixImports(serviceContent.trim(), CrudLayerImportConstants.SERVICE_IMPORT_WHITELIST);
                    Path serviceFile = serviceDir.resolve(serviceName + ".java");
                    Files.writeString(serviceFile, fixedServiceContent);
                    log.debug("Wrote service file: {}", serviceFile);
                }
            }

            // 提取并写入控制器类
            String controllerContent = extractControllerContentFromResponse(cleanedContent);
            if (controllerContent != null && !controllerContent.trim().isEmpty()) {
                String controllerName = classNames.get("controller");
                if (controllerName == null || controllerName.trim().isEmpty()) {
                    controllerName = "UnknownController";
                    log.warn("Could not extract controller name, using default: {}", controllerName);
                }
                // 确保控制器名称不包含非法字符
                String safeControllerName = controllerName.replaceAll("[^a-zA-Z0-9_]", "_");
                String controllerFileName = safeControllerName + ".java";
                // 使用AI修复导入语句
                    String fixedControllerContent = aiRepairService.fixImports(controllerContent.trim(), CrudLayerImportConstants.CONTROLLER_IMPORT_WHITELIST);
                Path controllerFile = controllerDir.resolve(controllerFileName);
                Files.writeString(controllerFile, fixedControllerContent);
                log.debug("Wrote controller file: {}", controllerFile);
            }

            // 提取并写入请求类
            String requestContent = extractRequestClassesFromResponse(cleanedContent);
            if (requestContent != null && !requestContent.trim().isEmpty()) {
                String requestClassName = classNames.get("requestClasses");
                if (requestClassName != null) {
                    // 使用AI修复导入语句
                    String fixedRequestContent = aiRepairService.fixImports(requestContent.trim(), CrudLayerImportConstants.REQUEST_IMPORT_WHITELIST);
                    Path requestFile = requestDir.resolve(requestClassName + ".java");
                    Files.writeString(requestFile, fixedRequestContent);
                    log.debug("Wrote request classes file: {}", requestFile);
                }
            }

            // 提取并写入返回类
            String responseClassesContent = extractResponseClassesFromResponse(cleanedContent);
            if (responseClassesContent != null && !responseClassesContent.trim().isEmpty()) {
                String responseClassName = classNames.get("responseClasses");
                if (responseClassName != null) {
                    // 使用AI修复导入语句
                    String fixedResponseContent = aiRepairService.fixImports(responseClassesContent.trim(), CrudLayerImportConstants.RESPONSE_IMPORT_WHITELIST);
                    Path responseFile = responseDir.resolve(responseClassName + ".java");
                    Files.writeString(responseFile, fixedResponseContent);
                    log.debug("Wrote response classes file: {}", responseFile);
                }
            }

            log.debug("Successfully parsed and wrote workflow response to standard directories");
            return true;
        } catch (IOException e) {
            log.error("Failed to parse and write workflow response: {}", e.getMessage(), e);
            return false;
        }
    }

    
     /**
     * 提取指定部分的代码
     * @param text 完整文本内容
     * @param sectionName 部分名称
     * @return 提取的代码部分
     */
    private String extractSection(String text, String sectionName) {
        // 标准化文本，去除所有标记前的转义字符
        String normalizedText = text.replaceAll("\\\\(### ---)", "$1");
        
        String startTag = "### ---" + sectionName + "_START---";
        String endTag = "### ---" + sectionName + "_END---";
        
        int start = normalizedText.indexOf(startTag);
        int end = normalizedText.indexOf(endTag);
        if (start == -1 || end == -1) return "";
        
        // 提取内容并清理Markdown格式标记
        String extractedContent = normalizedText.substring(start + startTag.length(), end).trim();
        return MarkdownCleaner.clean(extractedContent);
    }

    /**
     * 提取类名映射
     */
    private Map<String, String> extractClassNames(String text) {
        Map<String, String> names = new HashMap<>();
        
        // 标准化文本，去除所有标记前的转义字符
        String normalizedText = text.replaceAll("\\\\(### ---)", "$1");
        
        String start = "### ---CLASS_NAMES_START---";
        String end = "### ---CLASS_NAMES_END---";
        
        int s = normalizedText.indexOf(start);
        int e = normalizedText.indexOf(end);
        if (s == -1 || e == -1) return names;

        // 提取类名块内容并清理Markdown格式标记
        String block = normalizedText.substring(s + start.length(), e).trim();
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
     * 从响应中提取控制器内容
     */
    private String extractControllerContentFromResponse(String responseContent) {
        return extractSection(responseContent, "CONTROLLER");
    }


    /**
     * 从响应中提取仓库接口内容
     */
    private String extractRepositoryFromResponse(String responseContent) {
        return extractSection(responseContent, "REPOSITORY");
    }

    /**
     * 从响应中提取服务类内容
     */
    private String extractServiceFromResponse(String responseContent) {
        return extractSection(responseContent, "SERVICE");
    }

    /**
     * 从响应中提取请求类内容
     */
    private String extractRequestClassesFromResponse(String responseContent) {
        return extractSection(responseContent, "REQUEST_CLASSES");
    }

    /**
     * 从响应中提取返回类内容
     */
    private String extractResponseClassesFromResponse(String responseContent) {
        return extractSection(responseContent, "RESPONSE_CLASSES");
    }

    private void ensureDirectoryExists(String directoryPath) throws IOException {
        Path path = Path.of(directoryPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            log.debug("Created directory: {}", directoryPath);
        }
    }
}