// 包：com.example.ai.project.fileprocess

package com.example.ai.orchestration.fileprocess.registry;

import org.springframework.stereotype.Component;

import com.example.ai.orchestration.fileprocess.WorkflowResponseParser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PostConstruct;
/**
 * 解析器注册表：通过静态方法提供全局访问
 * 实例仍由 Spring 管理，避免 new 导致 @Autowired 失效
 */
@Component
public class WorkflowParserRegistry {

    private static final Map<String, WorkflowResponseParser> PARSERS = new ConcurrentHashMap<>();

    private final Map<String, WorkflowResponseParser> parsers;

    public WorkflowParserRegistry(Map<String, WorkflowResponseParser> parsers) {
        this.parsers = parsers;
    }

    /**
     * 应用启动后，将所有 Spring Bean 注册到静态 Map
     */
    @PostConstruct
    public void init() {
        PARSERS.clear(); // 安全起见

        for (Map.Entry<String, WorkflowResponseParser> entry : parsers.entrySet()) {
            String beanName = entry.getKey();
            WorkflowResponseParser parser = entry.getValue();

            if ("crudWorkflowResponseParser".equals(beanName)) {
                PARSERS.put("crud", parser);
            } else if ("authWorkflowResponseParser".equals(beanName)) {
                PARSERS.put("auth", parser);
            }
            // 新增 AI 类型在这里添加判断
        }

        System.out.println("✅ WorkflowParserRegistry loaded parsers: " + PARSERS.keySet());
    }

    /**
     * 根据 AI 类型获取解析器
     */
    public static WorkflowResponseParser getParser(String aiType) {
        if (aiType == null) return null;
        return PARSERS.get(aiType.trim().toLowerCase());
    }

    /**
     * 检查是否支持该类型
     */
    public static boolean supports(String aiType) {
        return getParser(aiType) != null;
    }
}