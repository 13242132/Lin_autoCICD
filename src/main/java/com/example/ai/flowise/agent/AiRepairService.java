package com.example.ai.flowise.agent;

import com.example.ai.flowise.agent.dto.FlowiseRequest;
import com.example.ai.flowise.agent.dto.WorkflowResponse;
import com.example.ai.flowise.agent.dto.AiRepairRequest;
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
 * AI修复服务类，用于调用AI代码修复工作流
 */
@Service
@Slf4j
public class AiRepairService {

    private final String flowiseAiRepairApiUrl;
    private final String authToken;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper(); // 用于解析JSON

    /**
     * 构造函数
     * @param flowiseApiConfig Flowise API配置
     */
    public AiRepairService(FlowiseApiConfig flowiseApiConfig) {
        // 从配置中获取AI修复API的URL
        this.flowiseAiRepairApiUrl = flowiseApiConfig.getEndpoints().getAiRepairGenerator();
        this.authToken = flowiseApiConfig.getAuthToken();
    }

    /**
     * 修复代码
     * @param request AI修复请求
     * @return 代码修复响应
     * @throws IOException 如果修复代码失败
     */
    public WorkflowResponse repairCode(AiRepairRequest request) throws IOException {
        log.info("开始修复代码");

        try {
            log.info("发送请求到Flowise AI修复API: {}", flowiseAiRepairApiUrl);
            log.debug("内容长度: {}", request.getContent().length());

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
            FlowiseRequest requestBody = new FlowiseRequest(request.getContent());
            HttpEntity<FlowiseRequest> requestEntity = new HttpEntity<>(requestBody, headers);

            // 发送POST请求到Flowise
            String jsonResponse = restTemplate.postForObject(flowiseAiRepairApiUrl, requestEntity, String.class);
            log.info("收到Flowise AI修复API的响应");

            // 将JsonNode转换为ObjectNode
            JsonNode node = objectMapper.readTree(jsonResponse);
            ObjectNode jsonNode = (ObjectNode) node;
            if (jsonNode.has("text")) {
                // 如果响应中包含text字段，则将其值赋给responseContent
                String textContent = jsonNode.get("text").asText();
                WorkflowResponse response = new WorkflowResponse();
                response.setResponseContent(textContent);
                log.info("成功解析AI修复响应（使用text字段）");
                return response;
            } else {
                // 否则按原来的方式解析
                WorkflowResponse response = objectMapper.readValue(jsonResponse, WorkflowResponse.class);
                log.info("成功解析AI修复响应（使用responseContent字段）");
                return response;
            }

        } catch (Exception e) {
            log.error("修复代码时出错", e);
            throw new IOException("修复代码失败: " + e.getMessage(), e);
        }
    }

    
}