package com.example.demo.exception;

/**
 * 统一异常类，用于处理所有异常情况
 */
public class UnifiedException extends RuntimeException {
    
    public UnifiedException(String message) {
        super(message);
    }
    
    public UnifiedException(String message, Throwable cause) {
        super(message, cause);
    }
}