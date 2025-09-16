package com.example.ai.codegeneration.service.impl;

import com.example.ai.codegeneration.service.JwtGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class JwtGenerationServiceImpl implements JwtGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(JwtGenerationServiceImpl.class);

    @Override
    public boolean generateJwtClasses(Path targetDir) {
        try {
            // 创建目录结构
            Path utilDir = targetDir.resolve("src/main/java/com/example/demo/auth/util");
            Path interceptorDir = targetDir.resolve("src/main/java/com/example/demo/auth/interceptor");
            Path configDir = targetDir.resolve("src/main/java/com/example/demo/auth/config");
            Path annotationDir = targetDir.resolve("src/main/java/com/example/demo/auth/annotation");
            Path resolverDir = targetDir.resolve("src/main/java/com/example/demo/auth/resolver");

            Files.createDirectories(utilDir);
            Files.createDirectories(interceptorDir);
            Files.createDirectories(configDir);
            Files.createDirectories(annotationDir);
            Files.createDirectories(resolverDir);

            // 生成JwtUtil.java
            String jwtUtilContent = "package com.example.demo.auth.util;\n\n" +
                    "import io.jsonwebtoken.Jwts;\n" +
                    "import io.jsonwebtoken.SignatureAlgorithm;\n" +
                    "import io.jsonwebtoken.Claims;\n" +
                    "import io.jsonwebtoken.ExpiredJwtException;\n\n" +
                    "import java.util.Date;\n\n" +
                    "public class JwtUtil {\n\n" +
                    "    // 秘钥，可换成更安全的方式存储\n" +
                    "    private static final String SECRET_KEY = \"MySuperSecretKeyForJWTTokenGenerationThatIsMuchLongerAndMoreSecure123456789\";\n\n" +
                    "    // token 有效期：1 天\n" +
                    "    private static final long EXPIRATION = 24 * 60 * 60 * 1000;\n\n" +
                    "    // 生成 JWT\n" +
                    "    public static String generateToken(Long userId) {\n" +
                    "        return Jwts.builder()\n" +
                    "                .claim(\"userId\", userId)\n" +
                    "                .setIssuedAt(new Date())\n" +
                    "                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))\n" +
                    "                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)\n" +
                    "                .compact();\n" +
                    "    }\n\n" +
                    "    // 解析 JWT\n" +
                    "    public static Claims parseToken(String token) {\n" +
                    "        try {\n" +
                    "            return Jwts.parser()\n" +
                    "                    .setSigningKey(SECRET_KEY)\n" +
                    "                    .parseClaimsJws(token)\n" +
                    "                    .getBody();\n" +
                    "        } catch (ExpiredJwtException e) {\n" +
                    "            throw new RuntimeException(\"Token expired\", e);\n" +
                    "        } catch (Exception e) {\n" +
                    "            throw new RuntimeException(\"Invalid token\", e);\n" +
                    "        }\n" +
                    "    }\n" +
                    "                    }";

            // 生成JwtInterceptor.java
            String jwtInterceptorContent = "package com.example.demo.auth.interceptor;\n\n" +
                    "import com.example.demo.auth.util.JwtUtil;\n" +
                    "import org.springframework.stereotype.Component;\n" +
                    "import org.springframework.web.servlet.HandlerInterceptor;\n\n" +
                    "import jakarta.servlet.http.HttpServletRequest;\n" +
                    "import jakarta.servlet.http.HttpServletResponse;\n\n" +
                    "@Component\n" +
                    "public class JwtInterceptor implements HandlerInterceptor {\n\n" +
                    "    @Override\n" +
                    "    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {\n" +
                    "        String authHeader = request.getHeader(\"Authorization\");\n" +
                    "        if (authHeader != null && authHeader.startsWith(\"Bearer \")) {\n" +
                    "            String token = authHeader.substring(7);\n" +
                    "            try {\n" +
                    "                JwtUtil.parseToken(token);\n" +
                    "                return true;\n" +
                    "            } catch (Exception e) {\n" +
                    "                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);\n" +
                    "                response.getWriter().write(\"Invalid or expired token\");\n" +
                    "                return false;\n" +
                    "            }\n" +
                    "        } else {\n" +
                    "            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);\n" +
                    "            response.getWriter().write(\"Missing Authorization header\");\n" +
                    "            return false;\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";

            // 生成WebConfig.java
            String webConfigContent = "package com.example.demo.auth.config;\n" +
                    "\n" +
                    "import com.example.demo.auth.interceptor.JwtInterceptor;\n" +
                    "import com.example.demo.auth.resolver.CurrentUserIdResolver;\n" +
                    "import org.springframework.beans.factory.annotation.Autowired;\n" +
                    "import org.springframework.context.annotation.Configuration;\n" +
                    "import org.springframework.web.method.support.HandlerMethodArgumentResolver;\n" +
                    "import org.springframework.web.servlet.config.annotation.InterceptorRegistry;\n" +
                    "import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;\n" +
                    "\n" +
                    "import java.util.List;\n" +
                    "\n" +
                    "@Configuration\n" +
                    "public class WebConfig implements WebMvcConfigurer {\n" +
                    "\n" +
                    "    @Autowired\n" +
                    "    private JwtInterceptor jwtInterceptor;\n" +
                    "    \n" +
                    "    @Autowired\n" +
                    "    private CurrentUserIdResolver currentUserIdResolver;\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public void addInterceptors(InterceptorRegistry registry) {\n" +
                    "        registry.addInterceptor(jwtInterceptor)\n" +
                    "                .addPathPatterns(\"/api/**\")           // 拦截所有 API\n" +
                    "                .excludePathPatterns(\"/api/auth/**\"); // 排除登录/注册接口\n" +
                    "    }\n" +
                    "    \n" +
                    "    @Override\n" +
                    "    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {\n" +
                    "        resolvers.add(currentUserIdResolver);\n" +
                    "    }\n" +
                    "}";

            // 生成CurrentUserId.java
            String currentUserIdContent = "package com.example.demo.auth.annotation;\n" +
                    "\n" +
                    "import java.lang.annotation.ElementType;\n" +
                    "import java.lang.annotation.Retention;\n" +
                    "import java.lang.annotation.RetentionPolicy;\n" +
                    "import java.lang.annotation.Target;\n" +
                    "\n" +
                    "/**\n" +
                    " * 用于从JWT令牌中自动注入当前用户ID的注解\n" +
                    " */\n" +
                    "@Target(ElementType.PARAMETER)\n" +
                    "@Retention(RetentionPolicy.RUNTIME)\n" +
                    "public @interface CurrentUserId {\n" +
                    "}";

            // 生成CurrentUserIdResolver.java
            String currentUserIdResolverContent = "package com.example.demo.auth.resolver;\n" +
                    "\n" +
                    "import com.example.demo.auth.annotation.CurrentUserId;\n" +
                    "import com.example.demo.auth.util.JwtUtil;\n" +
                    "import io.jsonwebtoken.Claims;\n" +
                    "import jakarta.servlet.http.HttpServletRequest;\n" +
                    "import org.springframework.core.MethodParameter;\n" +
                    "import org.springframework.stereotype.Component;\n" +
                    "import org.springframework.web.bind.support.WebDataBinderFactory;\n" +
                    "import org.springframework.web.context.request.NativeWebRequest;\n" +
                    "import org.springframework.web.method.support.HandlerMethodArgumentResolver;\n" +
                    "import org.springframework.web.method.support.ModelAndViewContainer;\n" +
                    "\n" +
                    "/**\n" +
                    " * CurrentUserId注解的参数解析器\n" +
                    " */\n" +
                    "@Component\n" +
                    "public class CurrentUserIdResolver implements HandlerMethodArgumentResolver {\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public boolean supportsParameter(MethodParameter parameter) {\n" +
                    "        // 检查参数是否有@CurrentUserId注解且类型为Long\n" +
                    "        return parameter.hasParameterAnnotation(CurrentUserId.class) && \n" +
                    "               parameter.getParameterType().equals(Long.class);\n" +
                    "    }\n" +
                    "\n" +
                    "    @Override\n" +
                    "    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,\n" +
                    "                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {\n" +
                    "        // 从请求中获取Authorization头\n" +
                    "        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);\n" +
                    "        String authHeader = request.getHeader(\"Authorization\");\n" +
                    "        \n" +
                    "        if (authHeader != null && authHeader.startsWith(\"Bearer \")) {\n" +
                    "            String token = authHeader.substring(7);\n" +
                    "            try {\n" +
                    "                // 解析JWT令牌\n" +
                    "                Claims claims = JwtUtil.parseToken(token);\n" +
                    "                // 从令牌中提取userId\n" +
                    "                return claims.get(\"userId\", Long.class);\n" +
                    "            } catch (Exception e) {\n" +
                    "                throw new RuntimeException(\"无法解析用户ID: \" + e.getMessage());\n" +
                    "            }\n" +
                    "        }\n" +
                    "        \n" +
                    "        throw new RuntimeException(\"缺少有效的Authorization头\" );\n" +
                    "    }\n" +
                    "}";

            // 写入文件
            Files.writeString(utilDir.resolve("JwtUtil.java"), jwtUtilContent);
            Files.writeString(interceptorDir.resolve("JwtInterceptor.java"), jwtInterceptorContent);
            Files.writeString(configDir.resolve("WebConfig.java"), webConfigContent);
            Files.writeString(annotationDir.resolve("CurrentUserId.java"), currentUserIdContent);
            Files.writeString(resolverDir.resolve("CurrentUserIdResolver.java"), currentUserIdResolverContent);

            logger.info("Successfully generated JWT classes in directory: {}", targetDir);
            return true;

        } catch (IOException e) {
            logger.error("Failed to generate JWT classes: {}", e.getMessage(), e);
            return false;
        }
    }
}