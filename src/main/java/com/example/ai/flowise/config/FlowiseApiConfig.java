package com.example.ai.flowise.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "flowise.api")
public class FlowiseApiConfig {

    private Endpoints endpoints = new Endpoints();
    private String authToken;

    public Endpoints getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Endpoints endpoints) {
        this.endpoints = endpoints;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @ConfigurationProperties(prefix = "flowise.api.endpoints")
    public static class Endpoints {
        private String apiDocGenerator;
        private String workflowGenerator;
        private String entityGenerator;
        private String requirementDocumentGenerator;
        private String prototypeGenerator;
        private String testGenerator;
        private String dataSqlGenerator;
        private String aiRepairGenerator;
        

        public String getApiDocGenerator() {
            return apiDocGenerator;
        }

        public void setApiDocGenerator(String apiDocGenerator) {
            this.apiDocGenerator = apiDocGenerator;
        }

        public String getWorkflowGenerator() {
            return workflowGenerator;
        }

        public void setWorkflowGenerator(String workflowGenerator) {
            this.workflowGenerator = workflowGenerator;
        }

        public String getEntityGenerator() {
            return entityGenerator;
        }

        public void setEntityGenerator(String entityGenerator) {
            this.entityGenerator = entityGenerator;
        }

        public String getRequirementDocumentGenerator() {
            return requirementDocumentGenerator;
        }

        public void setRequirementDocumentGenerator(String requirementDocumentGenerator) {
            this.requirementDocumentGenerator = requirementDocumentGenerator;
        }

        public String getPrototypeGenerator() {
            return prototypeGenerator;
        }

        public void setPrototypeGenerator(String prototypeGenerator) {
            this.prototypeGenerator = prototypeGenerator;
        }

        public String getTestGenerator() {
            return testGenerator;
        }

        public void setTestGenerator(String testGenerator) {
            this.testGenerator = testGenerator;
        }

        public String getDataSqlGenerator() {
            return dataSqlGenerator;
        }

        public void setDataSqlGenerator(String dataSqlGenerator) {
            this.dataSqlGenerator = dataSqlGenerator;
        }

        public String getAiRepairGenerator() {
            return aiRepairGenerator;
        }

        public void setAiRepairGenerator(String aiRepairGenerator) {
            this.aiRepairGenerator = aiRepairGenerator;
        }
    }
}