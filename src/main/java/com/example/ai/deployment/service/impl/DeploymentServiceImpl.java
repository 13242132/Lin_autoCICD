package com.example.ai.deployment.service.impl;

import com.example.ai.deployment.service.DeploymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Service
public class DeploymentServiceImpl implements DeploymentService {
    
    private static final Logger log = LoggerFactory.getLogger(DeploymentServiceImpl.class);
    
    private final ConcurrentHashMap<String, Process> runningProcesses = new ConcurrentHashMap<>();
    
    @Override
    public DeploymentService.DeploymentResponse deployProject(byte[] projectData) throws Exception {
        String processId = UUID.randomUUID().toString();
        log.info("开始部署项目，进程ID: {}", processId);
        
        Path tempDir = null;
        try {
            Path deployDir = Paths.get(System.getProperty("user.dir"), "deploy");
            log.info("部署目录路径: {}", deployDir);
            if (!Files.exists(deployDir)) {
                Files.createDirectories(deployDir);
            }
            
            tempDir = deployDir.resolve("project_" + processId.substring(0, 8));
            log.info("临时目录路径: {}", tempDir);
            if (!Files.exists(tempDir)) {
                Files.createDirectories(tempDir);
            }
            log.info("部署目录创建完成: {}", tempDir);
            
            Path zipFile = tempDir.resolve("project.zip");
            log.info("准备写入ZIP文件: {}", zipFile);
            Files.write(zipFile, projectData);
            log.info("项目zip文件已保存: {}", zipFile);
            
            log.info("开始解压项目文件到: {}", tempDir);
            unzipProject(zipFile, tempDir);
            log.info("项目解压完成");
            
            log.info("开始查找项目根目录在: {}", tempDir);
            Path projectRoot = findProjectRoot(tempDir);
            if (projectRoot == null) {
                String errorMsg = "未找到项目的pom.xml文件在目录: " + tempDir;
                log.error(errorMsg);
                return new DeploymentService.DeploymentResponse(false, errorMsg, processId, null);
            }
            log.info("项目根目录: {}", projectRoot);
            
            log.info("开始使用Maven构建项目在目录: {}", projectRoot);
            String buildOutput = buildProjectWithMaven(projectRoot);
            log.info("项目构建完成");
            
            log.info("开始启动Spring Boot应用在目录: {}", projectRoot);
            Process process = startSpringBootApp(projectRoot, processId);
            runningProcesses.put(processId, process);
            
            log.info("项目部署成功，进程ID: {}", processId);
            return new DeploymentService.DeploymentResponse(true, "项目部署成功", processId, buildOutput);
            
        } catch (Exception e) {
            String errorMsg = "项目部署失败: " + e.getMessage();
            log.error(errorMsg, e);
            // 部署失败时清理临时目录
            if (tempDir != null) {
                cleanupTempDirectory(tempDir);
            }
            // 返回详细的错误信息，包括堆栈跟踪
            String stackTrace = getStackTraceAsString(e);
            return new DeploymentService.DeploymentResponse(false, errorMsg, processId, stackTrace);
        }
    }
    
    /**
     * 将异常堆栈跟踪转换为字符串
     * @param e 异常对象
     * @return 堆栈跟踪字符串
     */
    private String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
    
    /**
     * 停止部署的应用程序
     */
    @Override
    public void shutdownProcess(String processId) throws Exception {
        log.info("尝试停止进程: {}", processId);
        Process process = runningProcesses.get(processId);
        if (process == null) {
            log.warn("未找到进程: {}", processId);
            throw new IllegalArgumentException("未找到指定的进程ID: " + processId);
        }
        
        try {
            // 在Windows上使用taskkill命令强制结束占用8090端口的进程
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("win")) {
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
                        log.info("进程已成功停止: {}", processId);
                        runningProcesses.remove(processId);
                        
                        // 清理临时目录
                        Path deployDir = Paths.get(System.getProperty("user.dir"), "deploy");
                        Path tempDir = deployDir.resolve("project_" + processId.substring(0, 8));
                        cleanupTempDirectory(tempDir);
                    } else {
                        log.error("停止进程失败，退出码: {}", exitCode);
                        throw new RuntimeException("停止进程失败，退出码: " + exitCode);
                    }
                } else {
                    // 如果没有找到占用8090端口的进程，尝试直接销毁进程对象
                    process.destroyForcibly();
                    log.info("进程已通过destroyForcibly停止: {}", processId);
                    runningProcesses.remove(processId);
                    
                    // 清理临时目录
                    Path deployDir = Paths.get(System.getProperty("user.dir"), "deploy");
                    Path tempDir = deployDir.resolve("project_" + processId.substring(0, 8));
                    cleanupTempDirectory(tempDir);
                }
            } else {
                // Unix/Linux系统
                process.destroyForcibly();
                log.info("进程已成功停止: {}", processId);
                runningProcesses.remove(processId);
                
                // 清理临时目录
                Path deployDir = Paths.get(System.getProperty("user.dir"), "deploy");
                Path tempDir = deployDir.resolve("project_" + processId.substring(0, 8));
                cleanupTempDirectory(tempDir);
            }
        } catch (Exception e) {
            log.error("停止进程时发生错误: ", e);
            throw e;
        }
    }
    
    private void unzipProject(Path zipFile, Path destDir) throws IOException {
        log.info("使用系统命令解压项目文件 {} 到 {}", zipFile, destDir);
        
        if (!Files.exists(destDir)) {
            Files.createDirectories(destDir);
        }
        
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(destDir.toFile());
            
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                processBuilder.command("powershell", "-Command", 
                    "Expand-Archive -Path '" + zipFile.toString() + "' -DestinationPath '" + destDir.toString() + "' -Force");
            } else {
                processBuilder.command("unzip", "-o", zipFile.toString(), "-d", destDir.toString());
            }
            
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                String errorMsg = "解压命令执行失败，退出码: " + exitCode;
                log.error(errorMsg);
                log.error("解压输出:\n{}", output.toString());
                throw new RuntimeException(errorMsg + "\n输出:\n" + output.toString());
            }
            
            log.info("解压命令执行成功，输出: {}", output.toString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("解压过程被中断", e);
        }
    }
    
    private Path findProjectRoot(Path tempDir) throws IOException {
        try (Stream<Path> paths = Files.walk(tempDir)) {
            return paths.filter(path -> path.getFileName().toString().equals("pom.xml"))
                    .map(Path::getParent)
                    .findFirst()
                    .orElse(null);
        }
    }
    
    private String buildProjectWithMaven(Path projectRoot) throws IOException, InterruptedException {
        log.info("在目录 {} 中使用Maven构建项目", projectRoot);
        
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(projectRoot.toFile());
        
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            processBuilder.command("cmd", "/c", "mvn", "clean", "package");
        } else {
            processBuilder.command("mvn", "clean", "package");
        }
        
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            String errorMsg = "Maven构建失败，退出码: " + exitCode;
            log.error(errorMsg);
            log.error("构建输出:\n{}", output.toString());
            throw new RuntimeException(errorMsg + "\n输出:\n" + output.toString());
        }
        
        return output.toString();
    }
    
    private Process startSpringBootApp(Path projectRoot, String processId) throws IOException {
        log.info("启动Spring Boot应用，进程ID: {}", processId);
        
        Path targetDir = projectRoot.resolve("target");
        if (!Files.exists(targetDir)) {
            throw new RuntimeException("未找到target目录");
        }
        
        Path jarFile = findJarFile(targetDir);
        if (jarFile == null) {
            throw new RuntimeException("未找到生成的jar文件");
        }
        
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(projectRoot.toFile());
        
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            processBuilder.command("cmd", "/c", "java", "-jar", jarFile.toString());
        } else {
            processBuilder.command("java", "-jar", jarFile.toString());
        }
        
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        
        // 捕获应用启动输出
        StringBuilder startupOutput = new StringBuilder();
        Thread outputThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null && process.isAlive()) {
                    log.info("[{}] {}", processId, line);
                    startupOutput.append(line).append("\n");
                }
            } catch (IOException e) {
                log.error("读取进程输出异常: ", e);
            }
        });
        outputThread.setDaemon(true);
        outputThread.start();
        
        try {
            Thread.sleep(5000); // 等待应用启动
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        if (!process.isAlive()) {
            String errorMsg = "应用启动失败";
            log.error(errorMsg);
            log.error("启动输出:\n{}", startupOutput.toString());
            throw new RuntimeException(errorMsg + "\n启动输出:\n" + startupOutput.toString());
        }
        
        return process;
    }
    
    private Path findJarFile(Path targetDir) throws IOException {
        try (Stream<Path> paths = Files.list(targetDir)) {
            return paths.filter(path -> path.toString().endsWith(".jar"))
                    .filter(path -> !path.toString().endsWith("-sources.jar"))
                    .filter(path -> !path.toString().endsWith("-javadoc.jar"))
                    .findFirst()
                    .orElse(null);
        }
    }
    
    /**
     * 清理临时目录及其内容
     * @param tempDir 要清理的临时目录
     */
    private void cleanupTempDirectory(Path tempDir) {
        try {
            if (Files.exists(tempDir)) {
                log.info("开始清理临时目录: {}", tempDir);
                Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            log.debug("已删除: {}", path);
                        } catch (IOException e) {
                            log.warn("删除文件失败: {}, 错误: {}", path, e.getMessage());
                        }
                    });
                log.info("临时目录清理完成: {}", tempDir);
            }
        } catch (IOException e) {
            log.error("清理临时目录失败: {}, 错误: {}", tempDir, e.getMessage());
        }
    }

}

    