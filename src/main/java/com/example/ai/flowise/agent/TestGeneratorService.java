package com.example.ai.flowise.agent;

import com.example.ai.flowise.agent.dto.FlowiseRequest;
import com.example.ai.flowise.agent.dto.WorkflowResponse;
import com.example.ai.flowise.config.FlowiseApiConfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

/**
 * 测试生成服务类，用于调用AI测试生成工作流
 */
@Service
@Slf4j
public class TestGeneratorService {

    private final String flowiseTestApiUrl;
    private final String authToken;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper(); // 用于解析JSON

    /**
     * 构造函数
     * @param flowiseApiConfig Flowise API配置
     */
    public TestGeneratorService(FlowiseApiConfig flowiseApiConfig) {
        // 从配置中获取测试生成API的URL
        this.flowiseTestApiUrl = flowiseApiConfig.getEndpoints().getTestGenerator();
        this.authToken = flowiseApiConfig.getAuthToken();
    }

    /**
     * 生成测试脚本
     * @param apiDocumentation 接口文档内容
     * @param projectName 项目名称
     * @return 测试生成响应
     * @throws IOException 如果生成测试脚本失败
     */
    public WorkflowResponse generateTestScript(String apiDocumentation, String projectName) throws IOException {
        log.info("开始生成测试脚本，项目: {}", projectName);

        try {
            log.info("发送请求到Flowise测试生成API: {}", flowiseTestApiUrl);
            log.debug("接口文档长度: {}", apiDocumentation.length());

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (authToken != null && !authToken.isEmpty()) {
                headers.setBearerAuth(authToken);
                log.debug("添加Bearer令牌认证到请求");
            } else {
                log.warn("Flowise API认证令牌未配置");
            }

            // 构建请求体
            String prompt = buildTestGenerationPrompt(apiDocumentation, projectName);
            FlowiseRequest requestBody = new FlowiseRequest(prompt);
            HttpEntity<FlowiseRequest> request = new HttpEntity<>(requestBody, headers);

            // 发送POST请求到Flowise
            String jsonResponse = restTemplate.postForObject(flowiseTestApiUrl, request, String.class);
            log.info("收到Flowise测试生成API的响应");

            // 处理响应
            JsonNode node = objectMapper.readTree(jsonResponse);
            WorkflowResponse response = new WorkflowResponse();

            // 优先检查text字段
            if (node.has("text")) {
                String textContent = node.get("text").asText();
                response.setResponseContent(textContent);
                log.info("成功解析测试生成响应（使用text字段）");
            } 
            // 其次检查responseContent字段
            else if (node.has("responseContent")) {
                String content = node.get("responseContent").asText();
                response.setResponseContent(content);
                log.info("成功解析测试生成响应（使用responseContent字段）");
            } 
            // 如果都没有，设置为空字符串避免null
            else {
                response.setResponseContent("");
                log.warn("测试生成响应中未找到text或responseContent字段");
            }

            return response;

        } catch (Exception e) {
            log.error("生成测试脚本时出错", e);
            throw new IOException("生成测试脚本失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 构建测试生成提示词
     */
    private String buildTestGenerationPrompt(String apiDocumentation, String projectName) {
        return String.format(
            "项目名称: %s\n\n接口文档:\n%s",
            projectName, apiDocumentation
        );
    }
    
    /**
     * 验证生成的测试脚本是否有效
     */
    public boolean validateTestScript(String testScript) {
        if (testScript == null || testScript.trim().isEmpty()) {
            return false;
        }
        
        // 基本的Python测试脚本验证
        return testScript.contains("import") && 
               (testScript.contains("pytest") || testScript.contains("unittest")) &&
               testScript.contains("def test_");
    }
}