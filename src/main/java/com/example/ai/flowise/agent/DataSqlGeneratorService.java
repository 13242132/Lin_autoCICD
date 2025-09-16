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
 * 数据SQL生成服务类，用于调用AI生成H2数据库的初始化数据
 */
@Service
@Slf4j
public class DataSqlGeneratorService {

    private final String flowiseDataSqlApiUrl;
    private final String authToken;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper(); // 用于解析JSON

    /**
     * 构造函数
     * @param flowiseApiConfig Flowise API配置
     */
    public DataSqlGeneratorService(FlowiseApiConfig flowiseApiConfig) {
        // 从配置中获取数据SQL生成API的URL
        this.flowiseDataSqlApiUrl = flowiseApiConfig.getEndpoints().getDataSqlGenerator();
        this.authToken = flowiseApiConfig.getAuthToken();
    }

    /**
     * 生成数据SQL
     * @param entityContent 实体内容
     * @return 数据SQL生成响应
     * @throws IOException 如果生成数据SQL失败
     */
    public WorkflowResponse generateDataSql(String entityContent) throws IOException {
        log.info("开始生成数据SQL");

        try {
            log.info("发送请求到Flowise数据SQL生成API: {}", flowiseDataSqlApiUrl);
            log.debug("实体内容: {}", entityContent);

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
            FlowiseRequest requestBody = new FlowiseRequest(entityContent);
            HttpEntity<FlowiseRequest> request = new HttpEntity<>(requestBody, headers);

            // 发送POST请求到Flowise
            String jsonResponse = restTemplate.postForObject(flowiseDataSqlApiUrl, request, String.class);
            log.info("收到Flowise数据SQL生成API的响应");

            // 处理响应中的text字段
            // 将JsonNode转换为ObjectNode
            JsonNode node = objectMapper.readTree(jsonResponse);
            ObjectNode jsonNode = (ObjectNode) node;
            if (jsonNode.has("text")) {
                // 如果响应中包含text字段，则将其值赋给responseContent
                String textContent = jsonNode.get("text").asText();
                WorkflowResponse response = new WorkflowResponse();
                response.setResponseContent(textContent);
                log.info("成功解析数据SQL生成响应（使用text字段）");
                return response;
            } else {
                // 否则按原来的方式解析
                WorkflowResponse response = objectMapper.readValue(jsonResponse, WorkflowResponse.class);
                log.info("成功解析数据SQL生成响应（使用responseContent字段）");
                return response;
            }

        } catch (Exception e) {
            log.error("生成数据SQL时出错", e);
            throw new IOException("生成数据SQL失败: " + e.getMessage(), e);
        }
    }
}