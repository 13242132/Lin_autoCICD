package com.example.ai.codegeneration.service.impl;

import com.example.ai.codegeneration.service.CodeGenerationService;
import com.example.ai.codegeneration.service.EntityProcessingService;
import com.example.ai.codegeneration.service.ExceptionGenerationService;
import com.example.ai.codegeneration.service.JwtGenerationService;
import com.example.ai.flowise.agent.ApiDocGeneratorService;
import com.example.ai.flowise.agent.InterfaceWorkflowGeneratorService;
import com.example.ai.flowise.agent.EntityGeneratorService;
import com.example.ai.flowise.agent.DataSqlGeneratorService;
import com.example.ai.flowise.agent.dto.ApiDocResponse;
import com.example.ai.flowise.agent.dto.WorkflowResponse;
import com.example.ai.orchestration.dto.ProjectGenerateRequest;
import com.example.ai.orchestration.fileprocess.FileProcessingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.zip.ZipOutputStream;

@Service
public class CodeGenerationServiceImpl implements CodeGenerationService {


    private static final Logger logger = LoggerFactory.getLogger(CodeGenerationServiceImpl.class);
    
    @Autowired
    private ApiDocGeneratorService apiDocGeneratorService;

    @Autowired
    private InterfaceWorkflowGeneratorService workflowGeneratorService;

    @Autowired
    private EntityGeneratorService entityGeneratorService;

    @Autowired
    private DataSqlGeneratorService dataSqlGeneratorService;

    @Autowired
    private FileProcessingService fileProcessingService;
    
    @Autowired
    private EntityProcessingService entityProcessingService;

    @Autowired
    private JwtGenerationService jwtGenerationService;
    
    @Autowired
    private ExceptionGenerationService exceptionGenerationService;

  

    @Override
    public byte[] generateProject(ProjectGenerateRequest request) throws Exception {
       
        try {
            // Create temporary directory for project generation
            Path tempDir = Files.createTempDirectory("project-generate-");
            logger.info("Created temporary directory for project generation: {}", tempDir);

            // 1. Generate project structure
            fileProcessingService.createProjectStructure(tempDir.toString());

            // 2. Generate Maven POM file
            String projectName = request.getProjectName() != null ? request.getProjectName() : "demo";
            boolean pomGenerated = fileProcessingService.generateMultiDatabasePomXml(tempDir.toString(), projectName);
            if (!pomGenerated) {
                logger.error("Failed to generate Maven POM file");
                throw new RuntimeException("Failed to generate Maven POM file");
            }

            // 3. Generate H2 database configuration file
            boolean h2ConfigGenerated = fileProcessingService.generateH2ConfigFile(tempDir.toString());
            if (!h2ConfigGenerated) {
                logger.error("Failed to generate H2 database configuration file");
                throw new RuntimeException("Failed to generate H2 database configuration file");
            }

            // 4. Generate API documentation
            String documentationInput = request.getFilesContentAsString();
            if (documentationInput.isEmpty()) {
                documentationInput = request.getProjectDescription() != null ? request.getProjectDescription() : "";
            }
            ApiDocResponse apiDoc = apiDocGeneratorService.generateApiDocs(documentationInput);

            
            // 切分AI回复用来进行并行调用接口生成工作流和生成完整的实体代码。
            // 5. 使用splitApiResponse方法切分AI回复
            String[] apiResponseParts = fileProcessingService.splitApiResponse(apiDoc.getAiResponse());
            logger.debug("AI response split into {} parts", apiResponseParts.length);

            // 5.1 将切分后的API响应部分写入到临时目录中的独立文件
            writeApiResponsePartsToDirectory(apiResponseParts, tempDir);

            // 5.2 将切分后的实体类别交给实体处理服务来进行实现。
            String completeEntityContent = null; // 用于保存完整的实体内容
            if (apiResponseParts.length > 0) {
                // 获取最后一部分作为实体列表
                String entityList = apiResponseParts[apiResponseParts.length - 1];
                try {
                    // 使用实体处理服务处理实体生成和文件写入
                    completeEntityContent = entityProcessingService.processEntities(entityList, entityGeneratorService, tempDir);
                } catch (Exception e) {
                    logger.error("实体处理失败", e);
                }
            } else {
                logger.warn("没有找到API响应部分来生成实体");
            }

            // 5.3 Generate data.sql for H2 database initialization
            if (completeEntityContent != null && !completeEntityContent.isEmpty()) {
                logger.info("开始调用数据SQL生成AI");
                try {
                    WorkflowResponse dataSqlResponse = dataSqlGeneratorService.generateDataSql(completeEntityContent);
                    logger.info("数据SQL生成AI调用成功");

                    // 使用MarkdownCleaner工具类清理数据SQL生成AI响应中的Markdown标记
                    String cleanedDataSqlResponse = com.example.ai.util.MarkdownCleaner.clean(dataSqlResponse.getResponseContent());
                    
                    // 将清理后的数据SQL生成AI响应写入临时目录的标准位置
                    Path dataSqlFile = tempDir.resolve("src/main/resources/data.sql");
                    Files.createDirectories(dataSqlFile.getParent());
                    Files.writeString(dataSqlFile, cleanedDataSqlResponse);
                    logger.info("数据SQL已写入文件: {}", dataSqlFile);
                    
                    // 创建包含AI回复的数据库文件
                    Path dbFile = tempDir.resolve("src/main/resources/db/migration/V1__Initial_schema.sql");
                    Files.createDirectories(dbFile.getParent());
                    String dbContent = cleanedDataSqlResponse;
                    Files.writeString(dbFile, dbContent);
                    logger.info("数据库文件已写入: {}", dbFile);
                } catch (Exception e) {
                    logger.error("调用数据SQL生成AI失败", e);
                }
            } else {
                logger.warn("没有找到完整的实体内容来生成数据SQL");
            }

               // 6. Generate API documentation
            boolean apiDocGenerated = fileProcessingService.generateApiDocumentation(tempDir.toString(), apiDoc.getAiResponse());
            if (!apiDocGenerated) {
                logger.error("Failed to generate API documentation");
                throw new RuntimeException("Failed to generate API documentation");
            }


            // 7.1生成内置工具类，拦截器，webconfig
            boolean jwtGenerated = jwtGenerationService.generateJwtClasses(tempDir);
            if (!jwtGenerated) {
                logger.error("Failed to generate JWT classes");
                throw new RuntimeException("Failed to generate JWT classes");
            }

            // 7.2步骤生成异常处理类。
            boolean exceptionGenerated = exceptionGenerationService.generateExceptionClasses(tempDir);
            if (!exceptionGenerated) {
                logger.error("Failed to generate SimpleException class");
                throw new RuntimeException("Failed to generate SimpleException class");
            }

        


            // 7.调用FileProcessingService处理并行工作流
            String combinedWorkflowResult = fileProcessingService.processWorkflowsInParallel(apiResponseParts, workflowGeneratorService, tempDir);
            logger.debug("Combined workflow result length: {}", combinedWorkflowResult.length());


        
            // 8. Generate application entry class
            boolean entryClassGenerated = fileProcessingService.generateApplicationEntryClass(tempDir.toString(), projectName);
            if (!entryClassGenerated) {
                logger.error("Failed to generate application entry class");
                throw new RuntimeException("Failed to generate application entry class");
            }
            

            // 9. Package into ZIP
            byte[] zipBytes = createZipFile(tempDir);

            // 10.Clean up temporary directory
            boolean deleted = fileProcessingService.deleteDirectory(tempDir.toString());
            if (deleted) {
                logger.info("Cleaned up temporary directory: {}", tempDir);
            } else {
                logger.warn("Failed to clean up temporary directory: {}", tempDir);
            }

            System.out.println("Project '" + projectName + "' generated successfully, returning as zip.");
            return zipBytes;

        } catch (Exception e) {
            logger.error("Failed to generate project zip", e);
            throw new RuntimeException("Failed to generate project zip", e);
        }
    }

     /**
     * 将切分后的API响应部分写入到目录中的独立文件
     */
    private void writeApiResponsePartsToDirectory(String[] apiResponseParts, Path tempDir) throws IOException {
        if (apiResponseParts == null || apiResponseParts.length == 0) {
            logger.info("No API response parts to write");
            return;
        }

        // 创建一个目录来存储API响应部分
        Path apiPartsDir = tempDir.resolve("api-response-parts");
        Files.createDirectories(apiPartsDir);
        logger.info("Created directory for API response parts: {}", apiPartsDir);

        // 遍历所有API响应部分并写入文件
        for (int i = 0; i < apiResponseParts.length; i++) {
            String partContent = apiResponseParts[i];
            String fileName = "api-response-part-" + (i + 1) + ".txt";
            Path filePath = apiPartsDir.resolve(fileName);

            try {
                Files.writeString(filePath, partContent);
                logger.debug("Wrote API response part {} to file: {}", (i + 1), filePath);
            } catch (IOException e) {
                logger.error("Failed to write API response part {} to file", (i + 1), e);
                throw new RuntimeException("Failed to write API response part to file", e);
            }
        }
    }

    /**
     * Creates a ZIP file from the project directory
     */
    private byte[] createZipFile(Path tempDir) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            // Walk through all files in the directory
            Files.walk(tempDir)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            // Create relative path for ZIP entry
                            String entryName = tempDir.relativize(file).toString();
                            // Add file to ZIP
                            fileProcessingService.addFileToZip(file.toString(), zos, entryName);
                        } catch (Exception e) {
                            logger.error("Failed to add file to ZIP: {}", file, e);
                            throw new RuntimeException("Failed to add file to ZIP", e);
                        }
                    });
        }

        return baos.toByteArray();
    }

}