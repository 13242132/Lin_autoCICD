package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器，统一处理所有异常并返回特定格式的JSON响应
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理统一异常
     */
    @ExceptionHandler(UnifiedException.class)
    public ResponseEntity<Map<String, String>> handleUnifiedException(UnifiedException e) {
        Map<String, String> response = new HashMap<>();
        
        // 根据异常消息判断错误类型
        String message = e.getMessage();
        String errorType = "INTERNAL_SERVER_ERROR";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        
        if (message != null) {
            if (message.contains("未授权") || message.contains("登录") || message.contains("认证")) {
                errorType = "UNAUTHORIZED";
                status = HttpStatus.UNAUTHORIZED;
            } else if (message.contains("不存在") || message.contains("未找到")) {
                errorType = "NOT_FOUND";
                status = HttpStatus.NOT_FOUND;
            } else if (message.contains("验证") || message.contains("参数")) {
                errorType = "BAD_REQUEST";
                status = HttpStatus.BAD_REQUEST;
            }
        }
        
        response.put("error", errorType);
        response.put("message", message);
        
        return ResponseEntity
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    /**
     * 处理其他未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "INTERNAL_SERVER_ERROR");
        response.put("message", "系统错误: " + e.getMessage());
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }
}