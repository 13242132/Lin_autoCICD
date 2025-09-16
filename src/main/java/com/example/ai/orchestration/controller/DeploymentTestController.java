package com.example.ai.orchestration.controller;

import com.example.ai.deployment.service.DeploymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/test")
public class DeploymentTestController {

    @Autowired
    private DeploymentService deploymentService;

    @GetMapping("/deploy")
    public String testDeployment() {
        try {
            // 部署前先停止占用8090端口的进程
            try {
                stopPort8090Process();
            } catch (Exception e) {
                // 停止端口进程失败不影响后续部署，仅记录日志
                System.out.println("停止8090端口进程时发生错误: " + e.getMessage());
            }
            
            // 读取测试项目的ZIP文件
            byte[] projectData = Files.readAllBytes(Paths.get("d:/code/flowise/Lin_autoCICD-main/zip/test-deployment-project.zip"));
            
            // 调用部署服务
            DeploymentService.DeploymentResponse response = deploymentService.deployProject(projectData);
            
            if (response.isSuccess()) {
                // 部署成功后停止进程
                try {
                    deploymentService.shutdownProcess(response.getProcessId());
                    return "部署成功并已停止进程！进程ID: " + response.getProcessId() + "\n输出信息:\n" + response.getOutput();
                } catch (Exception e) {
                    return "部署成功但停止进程失败！进程ID: " + response.getProcessId() + "\n停止错误: " + e.getMessage() + "\n输出信息:\n" + response.getOutput();
                }
            } else {
                return "部署失败: " + response.getMsg() + "\n输出信息:\n" + response.getOutput();
            }
        } catch (Exception e) {
            return "测试过程中发生错误: " + e.getMessage();
        }
    }
    
    /**
     * 停止占用8090端口的进程
     */
    private void stopPort8090Process() throws Exception {
        System.out.println("尝试停止占用8090端口的进程...");
        
        // 使用netstat和taskkill命令结束占用8090端口的进程
        ProcessBuilder findPortBuilder = new ProcessBuilder("cmd", "/c", "netstat -ano | findstr :8090");
        Process findPortProcess = findPortBuilder.start();
        
        // 读取命令输出
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(findPortProcess.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        
        // 等待命令执行完成
        findPortProcess.waitFor();
        
        // 解析输出获取PID
        String[] lines = output.toString().split("\n");
        String pid = null;
        for (String line : lines) {
            if (line.contains(":8090") && line.contains("LISTENING")) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length > 4) {
                    pid = parts[parts.length - 1];
                    break;
                }
            }
        }
        
        if (pid != null && !pid.isEmpty()) {
            // 使用taskkill命令结束进程
            ProcessBuilder killBuilder = new ProcessBuilder("taskkill", "/F", "/PID", pid);
            Process killProcess = killBuilder.start();
            int exitCode = killProcess.waitFor();
            
            if (exitCode == 0) {
                System.out.println("成功停止占用8090端口的进程，PID: " + pid);
            } else {
                throw new RuntimeException("停止进程失败，退出码: " + exitCode);
            }
        } else {
            System.out.println("未找到占用8090端口的进程");
        }
    }
    
    @GetMapping("/shutdown/{processId}")
    public String shutdownDeployment(@PathVariable String processId) {
        try {
            deploymentService.shutdownProcess(processId);
            return "成功停止进程，进程ID: " + processId;
        } catch (Exception e) {
            return "停止进程失败: " + e.getMessage();
        }
    }
}