package com.example.ai.deployment.service;

public interface DeploymentService {
    /**
     * 使用Maven部署项目
     * @param projectData 项目数据（zip格式）
     * @return 部署响应
     */
    DeploymentResponse deployProject(byte[] projectData) throws Exception;
    
    /**
     * 关闭部署的容器/进程
     * @param processId 进程ID
     */
    void shutdownProcess(String processId) throws Exception;
    
    /**
     * 部署响应类
     */
    class DeploymentResponse {
        private boolean success;
        private String message;
        private String processId;
        private String output;
        
        public DeploymentResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public DeploymentResponse(boolean success, String message, String processId, String output) {
            this.success = success;
            this.message = message;
            this.processId = processId;
            this.output = output;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMsg() {
            return message;
        }
        
        public String getProcessId() {
            return processId;
        }
        
        public String getOutput() {
            return output;
        }
    }
}