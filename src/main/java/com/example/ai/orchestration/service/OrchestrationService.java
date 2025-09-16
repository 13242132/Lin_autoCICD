package com.example.ai.orchestration.service;

import com.example.ai.orchestration.dto.ProjectGenerateRequest;

public interface OrchestrationService {
    byte[] processProject(ProjectGenerateRequest request) throws Exception;
}