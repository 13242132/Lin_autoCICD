package com.example.ai.codegeneration.service.impl;

import com.example.ai.codegeneration.service.ExceptionGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 异常处理类生成服务实现
 */
@Service
public class ExceptionGenerationServiceImpl implements ExceptionGenerationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExceptionGenerationServiceImpl.class);
    
    @Override
    public boolean generateExceptionClasses(Path projectDir) {
        try {
            // 创建异常包目录
            Path exceptionDir = projectDir.resolve("src/main/java/com/example/demo/exception");
            Files.createDirectories(exceptionDir);
            
            // 生成BusinessException类的代码
            String businessExceptionCode = "package com.example.demo.exception;\n\n" +
                "/**\n" +
                " * 业务异常类，用于所有业务逻辑中抛出异常\n" +
                " */\n" +
                "public class BusinessException extends RuntimeException {\n" +
                "    private final String code;\n" +
                "    \n" +
                "    /**\n" +
                "     * 构造一个带有错误码和详细消息的业务异常\n" +
                "     * @param code 错误码\n" +
                "     * @param message 异常的详细信息\n" +
                "     */\n" +
                "    public BusinessException(String code, String message) {\n" +
                "        super(message);\n" +
                "        this.code = code;\n" +
                "    }\n" +
                "    \n" +
                "    /**\n" +
                "     * 获取错误码\n" +
                "     * @return 错误码\n" +
                "     */\n" +
                "    public String getCode() {\n" +
                "        return code;\n" +
                "    }\n" +
                "}";
            
            // 生成ErrorResponse类的代码
            String errorResponseCode = "package com.example.demo.response;\n\n" +
                "import lombok.Data;\n" +
                "import lombok.NoArgsConstructor;\n" +
                "import lombok.AllArgsConstructor;\n\n" +
                "/**\n" +
                " * 错误响应类，用于统一返回错误信息\n" +
                " */\n" +
                "@Data\n" +
                "@NoArgsConstructor\n" +
                "@AllArgsConstructor\n" +
                "public class ErrorResponse {\n" +
                "    private String error;\n" +
                "    private String message;\n" +
                "}";
            
            // 生成GlobalExceptionHandler类的代码
            String globalExceptionHandlerCode = "package com.example.demo.exception;\n\n" +
                "import com.example.demo.response.ErrorResponse;\n" +
                "import org.springframework.http.HttpStatus;\n" +
                "import org.springframework.web.bind.annotation.ExceptionHandler;\n" +
                "import org.springframework.web.bind.annotation.ResponseStatus;\n" +
                "import org.springframework.web.bind.annotation.RestControllerAdvice;\n\n" +
                "/**\n" +
                " * 全局异常处理器，用于统一处理异常并返回ErrorResponse\n" +
                " */\n" +
                "@RestControllerAdvice\n" +
                "public class GlobalExceptionHandler {\n" +
                "    \n" +
                "    /**\n" +
                "     * 处理业务异常\n" +
                "     * @param ex 业务异常\n" +
                "     * @return 错误响应\n" +
                "     */\n" +
                "    @ExceptionHandler(BusinessException.class)\n" +
                "    @ResponseStatus(HttpStatus.BAD_REQUEST)\n" +
                "    public ErrorResponse handleBusinessException(BusinessException ex) {\n" +
                "        return new ErrorResponse(ex.getCode(), ex.getMessage());\n" +
                "    }\n" +
                "    \n" +
                "    /**\n" +
                "     * 处理通用异常\n" +
                "     * @param ex 通用异常\n" +
                "     * @return 错误响应\n" +
                "     */\n" +
                "    @ExceptionHandler(Exception.class)\n" +
                "    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)\n" +
                "    public ErrorResponse handleGenericException(Exception ex) {\n" +
                "        return new ErrorResponse(\"INTERNAL_ERROR\", ex.getMessage());\n" +
                "    }\n" +
                "}";
            
            // 创建response包目录
            Path responseDir = projectDir.resolve("src/main/java/com/example/demo/response");
            Files.createDirectories(responseDir);
            
            // 将BusinessException类写入到项目中
            Path businessExceptionFile = exceptionDir.resolve("BusinessException.java");
            Files.writeString(businessExceptionFile, businessExceptionCode);
            
            // 将ErrorResponse类写入到项目中
            Path errorResponseFile = responseDir.resolve("ErrorResponse.java");
            Files.writeString(errorResponseFile, errorResponseCode);
            
            // 将GlobalExceptionHandler类写入到项目中
            Path globalExceptionHandlerFile = exceptionDir.resolve("GlobalExceptionHandler.java");
            Files.writeString(globalExceptionHandlerFile, globalExceptionHandlerCode);
            
            logger.info("Exception classes generated successfully for the new project");
            return true;
        } catch (IOException e) {
            logger.error("Failed to generate exception classes for the new project", e);
            return false;
        }
    }
}