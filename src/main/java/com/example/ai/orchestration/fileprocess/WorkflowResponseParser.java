package com.example.ai.orchestration.fileprocess;

import java.nio.file.Path;
import java.util.Map;

public interface WorkflowResponseParser {
    boolean parseAndWrite(String responseContent, Path targetDir, Map<String, Object> sharedContext);
}