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
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * 工作流生成服务类，用于调用AI实体生成工作流
 */
@Service
@Slf4j
public class WorkflowGeneratorService {

    private final String flowiseWorkflowApiUrl;
    private final String authToken;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper(); // 用于解析JSON

    /**
     * 构造函数
     * @param flowiseApiConfig Flowise API配置
     */
    public WorkflowGeneratorService(FlowiseApiConfig flowiseApiConfig) {
        // 从配置中获取工作流生成API的URL
        this.flowiseWorkflowApiUrl = flowiseApiConfig.getEndpoints().getWorkflowGenerator();
        this.authToken = flowiseApiConfig.getAuthToken();
    }

    /**
     * 生成工作流
     * @param question 问题描述
     * @return 工作流响应
     * @throws IOException 如果生成工作流失败
     */
    public WorkflowResponse generateWorkflow(String question) throws IOException {
        log.info("开始生成工作流");

        try {
            log.info("发送请求到Flowise工作流API: {}", flowiseWorkflowApiUrl);
            log.debug("问题: {}", question);

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
            FlowiseRequest requestBody = new FlowiseRequest(question);
            HttpEntity<FlowiseRequest> request = new HttpEntity<>(requestBody, headers);

            // 发送POST请求到Flowise
            String jsonResponse = restTemplate.postForObject(flowiseWorkflowApiUrl, request, String.class);
            log.info("收到Flowise工作流API的响应");

            // 解析响应
            WorkflowResponse response = objectMapper.readValue(jsonResponse, WorkflowResponse.class);
            log.info("成功解析工作流响应");
            return response;

        } catch (Exception e) {
            log.error("生成工作流时出错", e);
            throw new IOException("生成工作流失败: " + e.getMessage(), e);
        }
    }
}