package com.example.ai.flowise.agent;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import com.example.ai.flowise.agent.dto.ApiDocResponse;
import com.example.ai.flowise.agent.dto.FlowiseRequest;
import com.example.ai.flowise.config.FlowiseApiConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Service
public class ApiDocGeneratorService {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiDocGeneratorService.class);
    
    private final String flowiseApiUrl;
    private final String authToken;
    private final RestTemplate restTemplate = new RestTemplate();

    public ApiDocGeneratorService(FlowiseApiConfig flowiseApiConfig) {
        this.flowiseApiUrl = flowiseApiConfig.getEndpoints().getApiDocGenerator();
        this.authToken = flowiseApiConfig.getAuthToken();
    }

    public ApiDocResponse generateApiDocs(String userDescription) throws IOException {
        try {
            logger.info("Sending request to Flowise API: {}", flowiseApiUrl);
            logger.debug("User description: {}", userDescription);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (authToken != null && !authToken.isEmpty()) {
                headers.setBearerAuth(authToken);
                logger.debug("Added Bearer token authentication to request");
            } else {
                logger.warn("Flowise API auth token is not configured");
            }

            // 构建请求体 - 直接发送用户描述，因为prompt已在Flowise中定义
            HttpEntity<FlowiseRequest> request = new HttpEntity<>(new FlowiseRequest(userDescription), headers);

            // 发送 POST 请求到 Flowise
            String jsonResponse = restTemplate.postForObject(flowiseApiUrl, request, String.class);
            logger.info("Received response from Flowise: {}", jsonResponse);

            // 提取AI的实际回复内容
            ObjectMapper objectMapper = new ObjectMapper();
            String aiResponseContent = objectMapper.readTree(jsonResponse).get("text").asText();
            logger.info("Extracted AI response content");

            // 创建ApiDocResponse对象并设置响应内容
            ApiDocResponse response = new ApiDocResponse();
            response.setAiResponse(aiResponseContent);
            logger.info("Set AI response to ApiDocResponse");

            return response;
            
        } catch (Exception e) {
            logger.error("Error generating API docs", e);
            throw new IOException("Failed to generate API documentation: " + e.getMessage(), e);
        }
    }


}