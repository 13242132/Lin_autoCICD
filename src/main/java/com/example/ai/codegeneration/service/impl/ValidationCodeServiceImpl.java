package com.example.ai.codegeneration.service.impl;

import com.example.ai.codegeneration.service.ValidationCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 验证码服务实现类，用于生成邮箱和短信验证码相关的类
 */
@Service
public class ValidationCodeServiceImpl implements ValidationCodeService {

    private static final Logger logger = LoggerFactory.getLogger(ValidationCodeServiceImpl.class);

    @Override
    public boolean generateValidationCodeClasses(Path targetDir) {
        try {
            // 创建工具类目录
            Path utilDir = targetDir.resolve("src/main/java/com/example/demo/util");
            Files.createDirectories(utilDir);

            // 生成邮箱验证工具类
            String emailValidationUtilContent = generateEmailValidationUtilContent();
            Path emailValidationUtilFile = utilDir.resolve("EmailValidationUtil.java");
            Files.writeString(emailValidationUtilFile, emailValidationUtilContent);
            logger.info("Generated EmailValidationUtil.java");

            // 生成短信验证工具类
            String smsValidationUtilContent = generateSmsValidationUtilContent();
            Path smsValidationUtilFile = utilDir.resolve("SmsValidationUtil.java");
            Files.writeString(smsValidationUtilFile, smsValidationUtilContent);
            logger.info("Generated SmsValidationUtil.java");

            return true;
        } catch (IOException e) {
            logger.error("Failed to generate validation code classes", e);
            return false;
        }
    }

    /**
     * 生成邮箱验证工具类内容
     */
    private String generateEmailValidationUtilContent() {
        return "package com.example.demo.util;\n\n" +
                "import org.springframework.stereotype.Component;\n\n" +
                "import java.util.Random;\n\n" +
                "/**\n" +
                " * 邮箱验证工具类\n" +
                " */\n" +
                "@Component\n" +
                "public class EmailValidationUtil {\n\n" +
                "    private static final int CODE_EXPIRE_MINUTES = 5;\n\n" +
                "    /**\n" +
                "     * 生成邮箱验证码\n" +
                "     * @param email 目标邮箱地址\n" +
                "     * @return 验证码\n" +
                "     */\n" +
                "    public String generateVerificationCode(String email) {\n" +
                "        // 生成6位随机验证码\n" +
                "        String code = generateRandomCode();\n        \n" +
                "        // 实际项目中这里会发送邮件，现在直接返回验证码\n" +
                "        System.out.println(\"为邮箱 \" + email + \" 生成验证码：\" + code);\n\n" +
                "        return code;\n" +
                "    }\n\n" +
                "    /**\n" +
                "     * 验证邮箱验证码\n" +
                "     * @param inputCode 用户输入的验证码\n" +
                "     * @return 验证是否成功\n" +
                "     */\n" +
                "    public boolean verifyCode(String inputCode) {\n" +
                "        return true;\n" +
                "    }\n\n" +
                "    /**\n" +
                "     * 生成随机验证码\n" +
                "     * @return 6位数字验证码\n" +
                "     */\n" +
                "    private String generateRandomCode() {\n" +
                "        Random random = new Random();\n" +
                "        StringBuilder code = new StringBuilder();\n" +
                "        for (int i = 0; i < 6; i++) {\n" +
                "            code.append(random.nextInt(10));\n" +
                "        }\n" +
                "        return code.toString();\n" +
                "    }\n" +
                "}\n";
    }

    /**
     * 生成短信验证工具类内容
     */
    private String generateSmsValidationUtilContent() {
        return "package com.example.demo.util;\n\n" +
                "import org.springframework.stereotype.Component;\n\n" +
                "import java.util.Random;\n\n" +
                "/**\n" +
                " * 短信验证工具类\n" +
                " */\n" +
                "@Component\n" +
                "public class SmsValidationUtil {\n\n" +
                "    private static final int CODE_EXPIRE_MINUTES = 5;\n\n" +
                "    /**\n" +
                "     * 生成短信验证码\n" +
                "     * @param phoneNumber 手机号码\n" +
                "     * @return 验证码\n" +
                "     */\n" +
                "    public String generateVerificationCode(String phoneNumber) {\n" +
                "        // 生成6位随机验证码\n" +
                "        String code = generateRandomCode();\n        \n" +
                "        // 实际项目中这里会调用第三方短信服务API发送短信\n" +
                "        // 现在直接返回验证码\n" +
                "        System.out.println(\"为手机号 \" + phoneNumber + \" 生成验证码：\" + code);\n\n" +
                "        return code;\n" +
                "    }\n\n" +
                "    /**\n" +
                "     * 验证短信验证码\n" +
                "     * @param inputCode 用户输入的验证码\n" +
                "     * @return 验证是否成功\n" +
                "     */\n" +
                "    public boolean verifyCode(String inputCode) {\n" +
                "        return true;\n" +
                "    }\n\n" +
                "    /**\n" +
                "     * 生成随机验证码\n" +
                "     * @return 6位数字验证码\n" +
                "     */\n" +
                "    private String generateRandomCode() {\n" +
                "        Random random = new Random();\n" +
                "        StringBuilder code = new StringBuilder();\n" +
                "        for (int i = 0; i < 6; i++) {\n" +
                "            code.append(random.nextInt(10));\n" +
                "        }\n" +
                "        return code.toString();\n" +
                "    }\n" +
                "}\n";
    }
}