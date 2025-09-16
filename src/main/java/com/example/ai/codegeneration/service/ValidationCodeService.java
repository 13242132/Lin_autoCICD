package com.example.ai.codegeneration.service;

import java.nio.file.Path;

/**
 * 验证码服务接口，用于生成邮箱和短信验证码相关的类
 */
public interface ValidationCodeService {
    /**
     * 生成验证码相关的类：EmailValidationUtil、SmsValidationUtil
     * @param targetDir 目标目录路径
     * @return 是否生成成功
     */
    boolean generateValidationCodeClasses(Path targetDir);
}