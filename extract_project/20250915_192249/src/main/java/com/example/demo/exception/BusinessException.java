package com.example.demo.exception;

/**
 * 业务异常类，用于所有业务逻辑中抛出异常
 */
public class BusinessException extends RuntimeException {
    private final String code;
    
    /**
     * 构造一个带有错误码和详细消息的业务异常
     * @param code 错误码
     * @param message 异常的详细信息
     */
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
    
    /**
     * 获取错误码
     * @return 错误码
     */
    public String getCode() {
        return code;
    }
}