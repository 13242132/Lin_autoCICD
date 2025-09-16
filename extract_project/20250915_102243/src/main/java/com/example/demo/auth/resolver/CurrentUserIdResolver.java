package com.example.demo.auth.resolver;

import com.example.demo.auth.annotation.CurrentUserId;
import com.example.demo.auth.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * CurrentUserId注解的参数解析器
 */
@Component
public class CurrentUserIdResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 检查参数是否有@CurrentUserId注解且类型为Long
        return parameter.hasParameterAnnotation(CurrentUserId.class) && 
               parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        // 从请求中获取Authorization头
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                // 解析JWT令牌
                Claims claims = JwtUtil.parseToken(token);
                // 从令牌中提取userId
                return claims.get("userId", Long.class);
            } catch (Exception e) {
                throw new RuntimeException("无法解析用户ID: " + e.getMessage());
            }
        }
        
        throw new RuntimeException("缺少有效的Authorization头" );
    }
}