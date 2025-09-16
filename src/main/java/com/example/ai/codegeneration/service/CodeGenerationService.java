package com.example.ai.codegeneration.service;

import com.example.ai.orchestration.dto.ProjectGenerateRequest;

public interface CodeGenerationService {
    byte[] generateProject(ProjectGenerateRequest request) throws Exception;
}