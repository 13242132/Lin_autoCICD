package com.example.ai.orchestration.fileprocess.impl;    // 使用WorkflowParserRegistry管理不同类型的解析器实例

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.ai.codegeneration.constant.ProjectConstants;
import com.example.ai.flowise.agent.InterfaceWorkflowGeneratorService;
import com.example.ai.flowise.agent.dto.WorkflowResponse;
import com.example.ai.orchestration.fileprocess.FileProcessingService;
import com.example.ai.orchestration.fileprocess.WorkflowResponseParser;
import com.example.ai.orchestration.fileprocess.registry.WorkflowParserRegistry;

import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class FileProcessingServiceImpl implements FileProcessingService {

    

    /**
     * 将AI回复写入文件
     * @param filePath 文件路径
     * @param content AI回复内容
     * @return 是否写入成功
     */
    public boolean writeAiResponseToFile(String filePath, String content) {
        if (filePath == null || content == null) {
            log.error("File path or content cannot be null");
            return false;
        }

        try {
            Path path = Paths.get(filePath);
            // 确保父目录存在
            ensureDirectoryExists(path.getParent().toString());
            // 写入文件
            Files.writeString(path, content);
            log.debug("Successfully wrote AI response to file: {}", filePath);
            return true;
        } catch (IOException e) {
            log.error("Failed to write AI response to file: {}", filePath, e);
            return false;
        }
    }

    /**
     * 将文件添加到压缩包
     * @param filePath 要添加的文件路径
     * @param zipOutputStream 压缩包输出流
     * @param entryName 压缩包中的条目名称
     * @return 是否添加成功
     */
    public boolean addFileToZip(String filePath, ZipOutputStream zipOutputStream, String entryName) {
        if (filePath == null || zipOutputStream == null || entryName == null) {
            log.error("File path, zip output stream or entry name cannot be null");
            return false;
        }

        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                log.error("File does not exist: {}", filePath);
                return false;
            }

            // 创建压缩包条目
            ZipEntry entry = new ZipEntry(entryName);
            zipOutputStream.putNextEntry(entry);

            // 写入文件内容
            byte[] content = Files.readAllBytes(path);
            zipOutputStream.write(content);
            zipOutputStream.closeEntry();

            log.debug("Successfully added file to zip: {}", entryName);
            return true;
        } catch (IOException e) {
            log.error("Failed to add file to zip: {}", entryName, e);
            return false;
        }
    }

    /**
     * 将AI回复直接写入压缩包
     * @param content AI回复内容
     * @param zipOutputStream 压缩包输出流
     * @param entryName 压缩包中的条目名称
     * @return 是否写入成功
     */
    public boolean writeAiResponseToZip(String content, ZipOutputStream zipOutputStream, String entryName) {
        if (content == null || zipOutputStream == null || entryName == null) {
            log.error("Content, zip output stream or entry name cannot be null");
            return false;
        }

        try {
            // 创建压缩包条目
            ZipEntry entry = new ZipEntry(entryName);
            zipOutputStream.putNextEntry(entry);

            // 写入内容
            zipOutputStream.write(content.getBytes());
            zipOutputStream.closeEntry();

            log.debug("Successfully wrote AI response to zip entry: {}", entryName);
            return true;
        } catch (IOException e) {
            log.error("Failed to write AI response to zip entry: {}", entryName, e);
            return false;
        }
    }

    /**
     * 确保目录存在
     * @param dirPath 目录路径
     */
    private void ensureDirectoryExists(String dirPath) {
        if (dirPath == null) {
            log.error("Directory path cannot be null");
            return;
        }

        Path path = Paths.get(dirPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                log.debug("Created directory: {}", dirPath);
            } catch (IOException e) {
                log.error("Failed to create directory: {}", dirPath, e);
                throw new RuntimeException("Failed to create directory: " + dirPath, e);
            }
        }
    }

    /**
     * 创建项目目录结构
     */
    @Override
    public void createProjectStructure(String baseDir) throws IOException {
        String[] directories = {
            "src/main/java/com/example/demo/entity",
            "src/main/java/com/example/demo/repository",
            "src/main/java/com/example/demo/service",
            "src/main/java/com/example/demo/controller",
            "src/main/java/com/example/demo/config",
            "src/main/resources",
            "src/test/java/com/example/demo"
        };
        
        for (String dir : directories) {
            Files.createDirectories(Paths.get(baseDir, dir));
        }
        log.debug("Created project structure under directory: {}", baseDir);
    }

    /**
     * 生成Maven POM文件
     * @param baseDir 基础目录
     * @param projectName 项目名称
     * @return 是否生成成功
     */
    @Override
    public boolean generateMultiDatabasePomXml(String baseDir, String projectName) {
        if (baseDir == null || projectName == null) {
            log.error("Base directory or project name cannot be null");
            return false;
        }

        // 检查项目名称是否包含中文，如果包含则使用默认英文名称
        String effectiveProjectName = projectName;
        if (projectName.matches(".*[\\u4e00-\\u9fa5].*")) {
            effectiveProjectName = "demo-project";
            log.warn("Project name contains Chinese characters, using default name: {}", effectiveProjectName);
        }

        try {
            // Get the POM content from our constants
            String pomContent = ProjectConstants.MAVEN_POM_CONTENT;
            // Replace the artifactId and name with the project name
            pomContent = pomContent.replace("disk-mode-test", effectiveProjectName.toLowerCase())
                    .replace("<name>disk-mode-test</name>", "<name>" + effectiveProjectName + "</name>");

            Path pomPath = Paths.get(baseDir, "pom.xml");
            // 确保父目录存在
            ensureDirectoryExists(pomPath.getParent().toString());
            Files.writeString(pomPath, pomContent);
            log.debug("Generated Maven POM file at: {}", pomPath);
            return true;
        } catch (IOException e) {
            log.error("Failed to generate Maven POM file: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 生成应用启动类
     * @param baseDir 基础目录
     * @param projectName 项目名称
     * @return 是否生成成功
     */
    @Override
    public boolean generateApplicationEntryClass(String baseDir, String projectName) {
        if (baseDir == null || projectName == null) {
            log.error("Base directory or project name cannot be null");
            return false;
        }

        // 检查项目名称是否包含中文，如果包含则使用默认英文名称
        String effectiveProjectName = projectName;
        if (projectName.matches(".*[\\u4e00-\\u9fa5].*")) {
            effectiveProjectName = "Demo";
            log.warn("Project name contains Chinese characters, using default name: {}", effectiveProjectName);
        }

        try {
            // 获取应用启动类内容
            String entryClassContent = ProjectConstants.APPLICATION_ENTRY_CLASS_CONTENT;
            // 替换占位符为项目名称
            entryClassContent = entryClassContent.replace("{0}", effectiveProjectName);

            // 应用启动类路径
            Path entryClassPath = Paths.get(baseDir, "src/main/java/com/example/demo/" + effectiveProjectName + "Application.java");
            // 确保父目录存在
            ensureDirectoryExists(entryClassPath.getParent().toString());
            Files.writeString(entryClassPath, entryClassContent);
            log.debug("Generated application entry class at: {}", entryClassPath);
            return true;
        } catch (IOException e) {
            log.error("Failed to generate application entry class: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 生成API文档
     * @param tempDir 临时目录
     * @param apiDoc API文档内容
     * @return 是否生成成功
     */
    @Override
    public boolean generateApiDocumentation(String tempDir, String apiDoc) {
        if (tempDir == null || apiDoc == null) {
            log.error("Temporary directory or API documentation content cannot be null");
            return false;
        }

        try {
            // 确保临时目录存在
            ensureDirectoryExists(tempDir);

            // 将API文档写入临时目录
            Path apiDocPath = Paths.get(tempDir, "api-documentation.md");
            Files.writeString(apiDocPath, apiDoc);
            log.debug("Generated API documentation at: {}", apiDocPath);
            return true;
        } catch (IOException e) {
            log.error("Failed to generate API documentation: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 删除目录及其所有内容
     * @param dirPath 目录路径
     * @return 是否删除成功
     */
    @Override
    public boolean deleteDirectory(String dirPath) {
        if (dirPath == null) {
            log.error("Directory path cannot be null");
            return false;
        }

        Path path = Paths.get(dirPath);
        if (!Files.exists(path)) {
            log.warn("Directory does not exist: {}", dirPath);
            return true; // 目录不存在视为删除成功
        }

        if (!Files.isDirectory(path)) {
            log.error("Path is not a directory: {}", dirPath);
            return false;
        }

        try {
            // 遍历目录，先删除文件，再删除目录
            Files.walk(path)
                    .sorted((a, b) -> b.compareTo(a)) // 逆序排序，确保先删除文件
                    .forEach(file -> {
                        try {
                            Files.delete(file);
                            log.debug("Deleted: {}", file);
                        } catch (IOException e) {
                            log.error("Failed to delete: {}", file, e);
                            throw new RuntimeException("Failed to delete file: " + file, e);
                        }
                    });

            log.debug("Successfully deleted directory: {}", dirPath);
            return true;
        } catch (IOException e) {
            log.error("Failed to walk directory: {}", dirPath, e);
            return false;
        } catch (RuntimeException e) {
            log.error("Error during directory deletion: {}", dirPath, e);
            return false;
        }
    }

    /**
     * 生成H2数据库配置文件
     * @param baseDir 基础目录
     * @return 是否生成成功
     */
    @Override
    public boolean generateH2ConfigFile(String baseDir) {
        if (baseDir == null) {
            log.error("Base directory cannot be null");
            return false;
        }

        try {
            // 获取H2配置内容
            String h2ConfigContent = ProjectConstants.H2_CONFIG_CONTENT;

            // 配置文件路径通常是src/main/resources/application.properties
            Path configPath = Paths.get(baseDir, "src/main/resources/application.properties");

            // 确保父目录存在
            ensureDirectoryExists(configPath.getParent().toString());

            // 写入配置文件
            Files.writeString(configPath, h2ConfigContent);
            log.debug("Generated H2 database configuration file at: {}", configPath);
            return true;
        } catch (IOException e) {
            log.error("Failed to generate H2 database configuration file: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 处理AI回复，根据---API_SEPARATOR---进行切分
     * @param aiResponse AI回复内容
     * @return 切分后的字符串数组
     */
    public String[] splitApiResponse(String aiResponse) {
        if (aiResponse == null) {
            log.error("AI response cannot be null");
            return new String[0];
        }

        // 确保最后一个接口和实体列表之间有分隔符
        String processedResponse = aiResponse;
        
        // 检查是否在---ENTITY_LIST_START---前缺少---API_SEPARATOR---
        int entityListStart = aiResponse.indexOf("---ENTITY_LIST_START---");
        if (entityListStart > 0) {
            // 查找最后一个basePath的位置
            int lastBasePathPos = aiResponse.lastIndexOf("basePath:");
            if (lastBasePathPos > 0 && lastBasePathPos < entityListStart) {
                // 提取最后一个basePath之后到---ENTITY_LIST_START---之前的内容
                String betweenBasePathAndEntityList = aiResponse.substring(lastBasePathPos, entityListStart).trim();
                
                // 检查这部分内容是否包含---API_SEPARATOR---
                if (!betweenBasePathAndEntityList.contains("---API_SEPARATOR---")) {
                    // 在---ENTITY_LIST_START---前添加分隔符
                    processedResponse = aiResponse.substring(0, entityListStart) + "\n---API_SEPARATOR---\n" + 
                                       aiResponse.substring(entityListStart);
                }
            }
        }
        
        // 使用---API_SEPARATOR---作为分隔符切分内容
        String[] parts = processedResponse.split("---API_SEPARATOR---");
        log.debug("Split AI response into {} parts", parts.length);
        return parts;
    }

    /**
     * 并行处理工作流
     * @param apiResponseParts 切分后的API响应部分
     * @param workflowGeneratorService 工作流生成器服务
     * @param tempDir 临时目录
     * @return 合并后的结果
     */
    @Override
    public String processWorkflowsInParallel(String[] apiResponseParts, 
                                          InterfaceWorkflowGeneratorService workflowGeneratorService, 
                                          Path tempDir) {
        try {
            // 1. 将所有 apiResponseParts 解析为 Map<controller, List<String>>
            Map<String, List<String>> groupedParts = new LinkedHashMap<>(); // 保持插入顺序

            // 提取最后一个部分作为实体类
            final String lastPart = apiResponseParts.length > 1 ? apiResponseParts[apiResponseParts.length - 1] : "";

            for (int i = 0; i < apiResponseParts.length - 1; i++) {
                String part = apiResponseParts[i];

                // 提取 controller 名称
                String controller = extractController(part);
                if (controller == null || controller.trim().isEmpty()) {
                    controller = "UnknownController"; // 容错
                }

                groupedParts.computeIfAbsent(controller, k -> new ArrayList<>()).add(part);
            }

            // 2. 并行处理每一组（每个 controller 一组）
            List<CompletableFuture<WorkflowResponse>> futures = new ArrayList<>();

            // 准备共享的附加内容
            String sharedSuffix = "\n" + lastPart + "\n" + ProjectConstants.MAVEN_POM_CONTENT;

            // 创建finalInput目录
            Path finalInputsDir = tempDir.resolve("final-inputs");
            Files.createDirectories(finalInputsDir);
            log.debug("Created final inputs directory: {}", finalInputsDir);

            for (Map.Entry<String, List<String>> group : groupedParts.entrySet()) {
                String controllerName = group.getKey();
                List<String> parts = group.getValue();

                CompletableFuture<WorkflowResponse> future = CompletableFuture.supplyAsync(() -> {
                    // 将该组所有接口拼接
                    String combinedGroup = String.join("\n\n---API_SEPARATOR---\n\n", parts);
                    // 添加实体类和 pom.xml
                    String finalInput = combinedGroup + sharedSuffix;

                    // 将finalInput写入文件
                    try {
                        String fileName = "final-input-" + controllerName + ".txt";
                        Path finalInputFilePath = finalInputsDir.resolve(fileName);
                        Files.writeString(finalInputFilePath, finalInput);
                        log.debug("Wrote final input to file: {}", finalInputFilePath);
                    } catch (IOException e) {
                        log.error("Failed to write final input to file for controller: {}", controllerName, e);
                    }

                    log.debug("Processing controller group: {} with {} interfaces. Input length: {}", controllerName, parts.size(), finalInput.length());
                    WorkflowResponse response = workflowGeneratorService.generateWorkflow(finalInput);
                    log.debug("Workflow response for group {}: {}", controllerName, response != null ? response.getResponseContent().length() : 0);
                    return response;
                });

                futures.add(future);
            }

            // 3. 等待所有任务完成
            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

            allOf.get(); // 等待所有任务完成
            StringBuilder combinedResult = new StringBuilder();

            // 创建工作流回复目录
            Path workflowResponsesDir = tempDir.resolve("workflow-responses");
            Files.createDirectories(workflowResponsesDir);
            log.debug("Created workflow responses directory: {}", workflowResponsesDir);

            int responseIndex = 1;
            for (CompletableFuture<WorkflowResponse> future : futures) {
                WorkflowResponse response = future.get();
                    //每一个控制器的完整实现，包括控制器，service，数据库层
                    String responseContent = response.getResponseContent();
                    log.debug("Response content length for response-{}: {}", responseIndex, responseContent.length());
                    combinedResult.append(responseContent).append("\n\n");

                    // 将回复保存到文件
                    Path responseFile = workflowResponsesDir.resolve("response-" + responseIndex + ".txt");
                    Files.writeString(responseFile, responseContent);
                    log.debug("Wrote workflow response to file: {}", responseFile);
                responseIndex++;
            }

            // 保留实体类定义
            if (!lastPart.isEmpty()) {
                combinedResult.append(lastPart).append("\n");
            }

            // 收集所有响应到列表
            List<WorkflowResponse> allResponses = new ArrayList<>();
            for (CompletableFuture<WorkflowResponse> future : futures) {
                allResponses.add(future.get());
            }

            
            // 调用解析方法将响应写入标准目录
            boolean parsedSuccessfully = true;
            Map<String, Object> sharedContext = new HashMap<>();
            
            for (WorkflowResponse response : allResponses) {
                if (response != null && response.getResponseContent() != null) {
                    String responseContent = response.getResponseContent();
                    String aiType = determineAiType(responseContent);
                    log.debug("AI type determined for response: {}", aiType);
                    WorkflowResponseParser parser = WorkflowParserRegistry.getParser(aiType);
                    if (parser != null) {
                        boolean currentSuccess = parser.parseAndWrite(
                            responseContent, tempDir, sharedContext);
                        parsedSuccessfully = parsedSuccessfully && currentSuccess;
                    } else {
                        log.error("No suitable parser found for AI type: {}", aiType);
                        parsedSuccessfully = false;
                    }
                }
            }
            
            
            if (!parsedSuccessfully) {
                log.error("Failed to parse and write workflow responses to standard directories");
            }

            // 保存合并结果
            Path combinedResultFile = workflowResponsesDir.resolve("combined-responses.txt");
            Files.writeString(combinedResultFile, combinedResult.toString());
            log.debug("Wrote combined workflow responses to file: {}", combinedResultFile);

            return combinedResult.toString();

        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to process workflow calls", e);
            throw new RuntimeException("Failed to process workflow calls", e);
        } catch (IOException e) {
            log.error("Failed to create directories or write files", e);
            throw new RuntimeException("Failed to create directories or write files", e);
        }
    }


    
    /**
     * 根据响应内容确定AI类型
     */
    private String determineAiType(String responseContent) {
        if (responseContent.contains("### ---CRUD_AI_OUTPUT_START---")) {
            return "crud";
        } else if (responseContent.contains("### ---AUTH_AI_OUTPUT_START---")) {
            return "auth";
        } else {
            log.warn("Unknown AI type in response content");
            return "unknown";
        }
    }

    /**
     * 提取控制器名称
     */
    private String extractController(String part) {
        return Arrays.stream(part.split("\n"))
            .map(String::trim)
            .filter(line -> line.startsWith("controller:"))
            .findFirst()
            .map(line -> line.substring("controller:".length()).trim())
            .orElse(null);
    }

    /**
     * 解析工作流响应并写入标准目录
     * @param responses 工作流响应列表
     * @param targetDir 目标目录
     * @return 是否解析并写入成功
     */
    @Override
    public boolean parseAndWriteWorkflowResponses(List<WorkflowResponse> responses, Path targetDir) {
        if (responses == null || targetDir == null) {
            log.error("Responses or target directory cannot be null");
            return false;
        }

        try {
            // 确保目标目录存在
            ensureDirectoryExists(targetDir.toString());

            // 定义标准目录结构
            Path entityDir = targetDir.resolve("src/main/java/com/example/demo/entity");
            Path repositoryDir = targetDir.resolve("src/main/java/com/example/demo/repository");
            Path serviceDir = targetDir.resolve("src/main/java/com/example/demo/service");
            Path controllerDir = targetDir.resolve("src/main/java/com/example/demo/controller");

            // 创建标准目录
            Files.createDirectories(entityDir);
            Files.createDirectories(repositoryDir);
            Files.createDirectories(serviceDir);
            Files.createDirectories(controllerDir);

            log.debug("Created standard directories under: {}", targetDir);

            // 解析并写入每个响应
            for (WorkflowResponse response : responses) {
                if (response == null || response.getResponseContent() == null) {
                    log.warn("Skipping null workflow response");
                    continue;
                }

                String responseContent = response.getResponseContent();
                
                // 提取类名映射
                Map<String, String> classNames = extractClassNames(responseContent);
                
              
                // 提取并写入实体类
                String entityContent = extractEntityFromResponse(responseContent);
                if (entityContent != null && !entityContent.trim().isEmpty()) {
                    String entityName = classNames.get("entity"); // Task
                    if (entityName != null) {
                        Path entityFile = entityDir.resolve(entityName + ".java");
                        Files.writeString(entityFile, entityContent);
                        log.debug("Wrote entity file: {}", entityFile);
                    }
                }

                // 提取并写入仓库接口
                String repositoryContent = extractRepositoryFromResponse(responseContent);
                if (repositoryContent != null && !repositoryContent.trim().isEmpty()) {
                    String repositoryName = classNames.get("repository"); // TaskRepository
                    if (repositoryName != null) {
                        Path repositoryFile = repositoryDir.resolve(repositoryName + ".java");
                        Files.writeString(repositoryFile, repositoryContent);
                        log.debug("Wrote repository file: {}", repositoryFile);
                    }
                }

                // 提取并写入服务类
                String serviceContent = extractServiceFromResponse(responseContent);
                if (serviceContent != null && !serviceContent.trim().isEmpty()) {
                    String serviceName = classNames.get("service"); // TaskService
                    if (serviceName != null) {
                        Path serviceFile = serviceDir.resolve(serviceName + ".java");
                        Files.writeString(serviceFile, serviceContent);
                        log.debug("Wrote service file: {}", serviceFile);
                    }
                }

                // 提取并写入控制器类
                String controllerContent = extractControllerContentFromResponse(responseContent);
                if (controllerContent != null && !controllerContent.trim().isEmpty()) {
                  String controllerName = classNames.get("controller"); // TaskController
                    if (controllerName == null || controllerName.trim().isEmpty()) {
                        controllerName = "UnknownController";
                        log.warn("Could not extract controller name, using default: {}", controllerName);
                    }
                    // 确保控制器名称不包含非法字符
                    String safeControllerName = controllerName.replaceAll("[^a-zA-Z0-9_]", "_");
                    String controllerFileName = safeControllerName + ".java";
                    Path controllerFile = controllerDir.resolve(controllerFileName);
                    Files.writeString(controllerFile, controllerContent);
                    log.debug("Wrote controller file: {}", controllerFile);
                }
            }

            log.debug("Successfully parsed and wrote {} workflow responses to standard directories", responses.size());
            return true;
        } catch (IOException e) {
            log.error("Failed to parse and write workflow responses: {}", e.getMessage(), e);
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
        String startTag = "### ---" + sectionName + "_START---";
        String endTag = "### ---" + sectionName + "_END---";
        int start = text.indexOf(startTag);
        int end = text.indexOf(endTag);
        if (start == -1 || end == -1) return "";
        return text.substring(start + startTag.length(), end).trim();
    }

    /**
     * 提取类名映射
     */
    private Map<String, String> extractClassNames(String text) {
        Map<String, String> names = new HashMap<>();
        String start = "### ---CLASS_NAMES_START---";
        String end = "### ---CLASS_NAMES_END---";
        
        int s = text.indexOf(start);
        int e = text.indexOf(end);
        if (s == -1 || e == -1) return names;

        String block = text.substring(s + start.length(), e).trim();
        for (String line : block.split("\n")) {
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
     * 从响应中提取实体类内容
     */
    private String extractEntityFromResponse(String responseContent) {
        return extractSection(responseContent, "ENTITY");
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

    @Override
    public String[] splitEntityResponse(String entityResponse) {
        if (entityResponse == null || entityResponse.trim().isEmpty()) {
            return new String[0];
        }
        return entityResponse.split("// ---ENTITY_BOUNDARY---");
    }

    @Override
    public boolean writeEntityPartsToFiles(String[] entityParts, String targetDir) {
        if (entityParts == null) {
            return false;
        }
        try {
            Path targetPath = Paths.get(targetDir).resolve("src/main/java/com/example/demo/entity");
            if (!Files.exists(targetPath)) {
                Files.createDirectories(targetPath);
            }

            for (String entityPart : entityParts) {
                if (entityPart == null || entityPart.trim().isEmpty()) {
                    continue;
                }
                String className = extractClassName(entityPart);
                if (className != null) {
                    Path filePath = targetPath.resolve(className + ".java");
                    Files.writeString(filePath, entityPart.trim());
                    log.info("Successfully wrote entity to file: {}", filePath);
                } else {
                    log.warn("Could not extract class name from part: {}", entityPart.substring(0, Math.min(entityPart.length(), 100)));
                }
            }
            return true;
        } catch (IOException e) {
            log.error("Error writing entity files", e);
            return false;
        }
    }

    private String extractClassName(String content) {
        Pattern pattern = Pattern.compile("// Class: (\\w+)");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }

        pattern = Pattern.compile("public class (\\w+)");
        matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

}