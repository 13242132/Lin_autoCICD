package com.example.ai.orchestration.service.impl;


import com.example.ai.autotest.service.AutoTestService;
import com.example.ai.codegeneration.service.CodeGenerationService;
import com.example.ai.deployment.service.DeploymentService;
import com.example.ai.orchestration.dto.ProjectGenerateRequest;
import com.example.ai.orchestration.service.OrchestrationService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrchestrationServiceImpl implements OrchestrationService {

    @Autowired
    private CodeGenerationService codeGenerationService;


    @Autowired
    private DeploymentService deploymentService;

    @Override
    public byte[] processProject(ProjectGenerateRequest request) throws Exception {
        // 1. Code Generation
        byte[] projectData = codeGenerationService.generateProject(request);

        // 2. Auto Deployment
        DeploymentService.DeploymentResponse deploymentResponse = deploymentService.deployProject(projectData);
        if (!deploymentResponse.isSuccess()) {
            log.warn("部署失败: {}", deploymentResponse.getMsg());
            // 部署失败时返回原始项目数据
            return projectData;
        }
        
        log.info("部署成功，进程ID: {}", deploymentResponse.getProcessId());
        // 部署成功后停止进程
        try {
            deploymentService.shutdownProcess(deploymentResponse.getProcessId());
            log.info("已成功停止部署的进程，进程ID: {}", deploymentResponse.getProcessId());
        } catch (Exception e) {
            log.warn("停止部署进程时发生错误: {}", e.getMessage());
            // 停止进程失败不影响整体流程，仅记录警告日志
        }
        
        // 返回项目数据
        return projectData;
    }
}