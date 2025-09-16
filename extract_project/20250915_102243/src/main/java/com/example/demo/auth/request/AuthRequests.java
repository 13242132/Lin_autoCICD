package com.example.demo.auth.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

public class AuthRequests {

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;

        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式不合法")
        private String email;

        @NotBlank(message = "密码不能为空")
        @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$",
            message = "密码必须至少8位，包含大小写字母、数字和特殊字符"
        )
        private String password;
    }

    @Data
    public static class LoginRequest {
        @NotBlank(message = "用户名或邮箱不能为空")
        private String usernameOrEmail;

        @NotBlank(message = "密码不能为空")
        private String password;
    }
}
