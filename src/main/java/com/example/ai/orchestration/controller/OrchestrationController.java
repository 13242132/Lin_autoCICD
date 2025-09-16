package com.example.ai.orchestration.controller;

import com.example.ai.orchestration.dto.ProjectGenerateRequest;
import com.example.ai.orchestration.service.OrchestrationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v2/project")
@CrossOrigin(origins = "*")
public class OrchestrationController {

    @Autowired
    private OrchestrationService orchestrationService;

    @PostMapping("/generate-full")
    public ResponseEntity<byte[]> generateFullProject(@RequestBody ProjectGenerateRequest request) {
        try {
            byte[] zipData = orchestrationService.processProject(request);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            String filename = (request.getProjectName() != null ?
                    request.getProjectName().replaceAll("[^a-zA-Z0-9]", "-") :
                    "generated-project") + ".zip";
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(zipData.length);

            return new ResponseEntity<>(zipData, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("项目生成失败: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "Orchestration Service",
                "version", "1.0.0"
        ));
    }

    @GetMapping("/database-types")
    public ResponseEntity<Map<String, Object>> getDatabaseTypes() {
        return ResponseEntity.ok(Map.of(
                "supportedTypes", new String[]{"H2", "MySQL", "PostgreSQL", "SQLite"},
                "defaultType", "H2",
                "status", "success"
        ));
    }
}