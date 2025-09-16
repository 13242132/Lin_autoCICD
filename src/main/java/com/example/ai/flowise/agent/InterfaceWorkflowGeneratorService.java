package com.example.ai.flowise.agent;

import com.example.ai.flowise.agent.dto.FlowiseRequest;
import com.example.ai.flowise.agent.dto.WorkflowResponse;
import com.example.ai.flowise.config.FlowiseApiConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class InterfaceWorkflowGeneratorService {

   private final RestTemplate restTemplate = new RestTemplate();
  private final ObjectMapper objectMapper = new ObjectMapper(); // 用于解析JSON
    private final String flowiseWorkflowApiUrl;
    private final String authToken;

    public InterfaceWorkflowGeneratorService(FlowiseApiConfig flowiseApiConfig) {
        this.flowiseWorkflowApiUrl = flowiseApiConfig.getEndpoints().getWorkflowGenerator();
        this.authToken = flowiseApiConfig.getAuthToken();
    }

    /**
     * 根据接口文档生成接口的工作流调用
     * @param apiDocumentation 接口文档内容
     * @return 工作流调用响应
     */
    public WorkflowResponse generateWorkflow(String apiDocumentation) {
        log.info("开始生成接口工作流，文档长度: {}", apiDocumentation.length());

        try {
            // 创建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (authToken != null && !authToken.isEmpty()) {
                headers.setBearerAuth(authToken);
                log.debug("Added Bearer token authentication to request");
            } else {
                log.warn("Flowise API auth token is not configured");
            }

            // 创建请求体
            FlowiseRequest request = new FlowiseRequest("根据以下接口文档生成工作流调用: " + apiDocumentation);

            // 发送请求
            HttpEntity<FlowiseRequest> requestEntity = new HttpEntity<>(request, headers);
            String jsonResponse = restTemplate.postForObject(flowiseWorkflowApiUrl, requestEntity, String.class);
            
            // 解析响应并提取AI内容
            String aiContent = extractFinalAiContentFromResponse(jsonResponse);
         
            log.info("成功提取AI回复内容: {}", aiContent);

            // 构建最终响应
            WorkflowResponse workflowResponse = new WorkflowResponse();
            workflowResponse.setResponseContent(aiContent); // 只包含AI的实际回复
            return workflowResponse;

        } catch (Exception e) {
            log.error("生成接口工作流失败: {}", e.getMessage(), e);
            WorkflowResponse errorResponse = new WorkflowResponse();
            errorResponse.setResponseContent("生成工作流失败: " + e.getMessage());
            return errorResponse;
        }
    }

    /**
     * 从Flowise的JSON响应中提取最后一个AI节点的实际回复内容
     * @param jsonResponse Flowise返回的完整JSON字符串
     * @return 提取到的AI回复内容
     * @throws JsonProcessingException 如果JSON解析失败
     */
    private String extractFinalAiContentFromResponse(String jsonResponse) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode executedDataArray = rootNode.get("agentFlowExecutedData");

        if (executedDataArray == null || !executedDataArray.isArray()) {
            log.warn("响应中未找到 'agentFlowExecutedData' 数组");
            return "AI未返回有效内容。";
        }

        // 遍历所有节点，找到最后一个 agentAgentflow 节点
        String finalAiContent = "";
        for (JsonNode nodeData : executedDataArray) {
            String nodeId = nodeData.get("nodeId").asText();
            String nodeName = nodeData.get("nodeLabel").asText();
            JsonNode dataNode = nodeData.get("data");

            // 检查是否为Agent节点
            if ("agentAgentflow".equals(dataNode.get("name").asText())) {
                JsonNode outputNode = dataNode.get("output");
                JsonNode contentNode = outputNode.get("content");

                // 更新最终AI内容为最后一个Agent节点的输出
                if (contentNode != null && !contentNode.isNull()) {
                    finalAiContent = contentNode.asText();
                    log.debug("从节点 '{}' 提取到AI内容: {}", nodeName, finalAiContent);
                }
            }
        }

        // 如果没有找到有效的Agent节点输出，则返回默认值
        if (finalAiContent.isEmpty()) {
            log.warn("未找到有效的AI节点输出内容");
            return "AI未返回有效内容。";
        }

        return finalAiContent;
    }
}