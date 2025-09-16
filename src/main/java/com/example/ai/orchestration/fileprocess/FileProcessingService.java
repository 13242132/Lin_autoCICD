package com.example.ai.orchestration.fileprocess;

import java.io.IOException;
import java.util.zip.ZipOutputStream;

public interface FileProcessingService {
    /**
     * 将AI回复写入文件
     * @param filePath 文件路径
     * @param content AI回复内容
     * @return 是否写入成功
     */
    boolean writeAiResponseToFile(String filePath, String content);

    /**
     * 将文件添加到压缩包
     * @param filePath 要添加的文件路径
     * @param zipOutputStream 压缩包输出流
     * @param entryName 压缩包中的条目名称
     * @return 是否添加成功
     */
    boolean addFileToZip(String filePath, ZipOutputStream zipOutputStream, String entryName);

    /**
     * 将AI回复直接写入压缩包
     * @param content AI回复内容
     * @param zipOutputStream 压缩包输出流
     * @param entryName 压缩包中的条目名称
     * @return 是否写入成功
     */
    boolean writeAiResponseToZip(String content, ZipOutputStream zipOutputStream, String entryName);

         /**
     * 创建项目目录结构
         * @throws IOException 
     */
    void createProjectStructure(String baseDir) throws IOException;

    /**
     * 生成Maven POM文件
     * @param baseDir 基础目录
     * @param projectName 项目名称
     * @return 是否生成成功
     */
    boolean generateMultiDatabasePomXml(String baseDir, String projectName);

    /**
     * 生成应用启动类
     * @param baseDir 基础目录
     * @param projectName 项目名称
     * @return 是否生成成功
     */
    boolean generateApplicationEntryClass(String baseDir, String projectName);

    /**
     * 生成API文档
     * @param tempDir 临时目录
     * @param apiDoc API文档内容
     * @return 是否生成成功
     */
    boolean generateApiDocumentation(String tempDir, String apiDoc);

    /**
     * 删除目录及其所有内容
     * @param dirPath 目录路径
     * @return 是否删除成功
     */
    boolean deleteDirectory(String dirPath);

    /**
     * 生成H2数据库配置文件
     * @param baseDir 基础目录
     * @return 是否生成成功
     */
    boolean generateH2ConfigFile(String baseDir);

    /**
     * 处理AI回复，根据---API_SEPARATOR---进行切分
     * @param aiResponse AI回复内容
     * @return 切分后的字符串数组
     */
    String[] splitApiResponse(String aiResponse);

    /**
     * 处理实体回复，根据// ---ENTITY_BOUNDARY---进行切分
     * @param entityResponse 实体回复内容
     * @return 切分后的字符串数组
     */
    String[] splitEntityResponse(String entityResponse);

    /**
     * 将切分后的实体写入对应的实体文件
     * @param entityParts 切分后的实体数组
     * @param targetDir 目标目录
     * @return 是否写入成功
     */
    boolean writeEntityPartsToFiles(String[] entityParts, String targetDir);

    /**
     * 并行处理工作流
     * @param apiResponseParts 切分后的API响应部分
     * @param workflowGeneratorService 工作流生成器服务
     * @param tempDir 临时目录
     * @return 合并后的结果
     */
    String processWorkflowsInParallel(String[] apiResponseParts, 
                                      com.example.ai.flowise.agent.InterfaceWorkflowGeneratorService workflowGeneratorService, 
                                      java.nio.file.Path tempDir);

    /**
     * 解析工作流响应并写入标准目录
     * @param workflowResponses 工作流响应列表
     * @param targetDir 目标目录
     * @return 是否解析并写入成功
     */
    boolean parseAndWriteWorkflowResponses(java.util.List<com.example.ai.flowise.agent.dto.WorkflowResponse> workflowResponses, 
                                          java.nio.file.Path targetDir);

}