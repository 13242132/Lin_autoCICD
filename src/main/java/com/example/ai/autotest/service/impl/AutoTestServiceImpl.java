package com.example.ai.autotest.service.impl;

import com.example.ai.autotest.service.AutoTestService;
import com.example.ai.flowise.agent.TestGeneratorService;
import com.example.ai.flowise.agent.dto.WorkflowResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.Map;

@Service
@Slf4j
public class AutoTestServiceImpl implements AutoTestService {

    @Autowired
    private TestGeneratorService testGeneratorService;

    @Override
    public boolean runTests(byte[] projectData, String projectPath) throws Exception {
        // 1. 从ZIP数据中提取api-documentation.md文件
        String apiDocumentation = extractApiDocumentation(projectData);
        if (apiDocumentation == null || apiDocumentation.isEmpty()) {
            log.error("未能从项目数据中提取API文档");
            return false;
        }

        // 2. 检查项目路径是否有效
        if (projectPath == null || projectPath.isEmpty()) {
            log.error("项目路径无效");
            return false;
        }

        // 3. 构造基础URL（假设Spring Boot应用运行在8080端口）
        String baseUrl = "http://localhost:8080";
        log.info("使用基础URL进行测试: {}", baseUrl);

        // 4. 调用TestGeneratorService生成测试脚本
        WorkflowResponse testResponse = testGeneratorService.generateTestScript(apiDocumentation, "test-project");
        if (testResponse == null || testResponse.getResponseContent() == null) {
            log.error("测试脚本生成失败");
            return false;
        }

        String testScript = testResponse.getResponseContent();
        log.debug("生成的测试脚本长度: {}", testScript.length());

        // 5. 将测试脚本保存到auto_test目录
        Path testDir = Paths.get("auto_test");
        // 确保目录存在
        if (!Files.exists(testDir)) {
            Files.createDirectories(testDir);
        }
        Path testScriptFile = testDir.resolve("test_api.py");
        Files.writeString(testScriptFile, testScript);
        log.info("测试脚本已保存到: {}", testScriptFile);

        // 6. 运行测试脚本
        boolean testsPassed = executeTestScript(testScriptFile, baseUrl);
        
        // 7. 清理测试脚本文件（保留目录）
        try {
            Files.deleteIfExists(testScriptFile);
        } catch (IOException e) {
            log.warn("清理测试脚本文件时出错: {}", e.getMessage());
        }

        return testsPassed;
    }

    /**
     * 从ZIP数据中提取api-documentation.md文件内容
     */
    private String extractApiDocumentation(byte[] projectData) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(projectData);
             ZipInputStream zis = new ZipInputStream(bais)) {
            
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if ("api-documentation.md".equals(entry.getName())) {
                    // 读取文件内容
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }
                    return baos.toString("UTF-8");
                }
                // 关闭当前条目
                zis.closeEntry();
            }
        }
        return null;
    }

    /**
     * 执行测试脚本
     */
    private boolean executeTestScript(Path testScriptFile, String baseUrl) throws IOException, InterruptedException {
        // 构造命令
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("python", testScriptFile.toString());
        
        // 设置环境变量
        Map<String, String> environment = processBuilder.environment();
        environment.put("BASE_URL", baseUrl);
        
        // 重定向输出
        processBuilder.redirectErrorStream(true);
        
        // 启动进程
        Process process = processBuilder.start();
        
        // 等待进程完成（最多等待5分钟）
        boolean finished = process.waitFor(5, TimeUnit.MINUTES);
        
        if (!finished) {
            log.warn("测试脚本执行超时，正在销毁进程");
            process.destroyForcibly();
            return false;
        }
        
        int exitCode = process.exitValue();
        log.info("测试脚本执行完成，退出码: {}", exitCode);
        
        return exitCode == 0;
    }
}