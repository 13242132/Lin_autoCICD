package com.example.ai.autotest.service;

public interface AutoTestService {
    boolean runTests(byte[] projectData, String projectPath) throws Exception;
}