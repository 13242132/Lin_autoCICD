package com.example.demo.auth.service;

import com.example.demo.auth.repository.AuthRepository;
import com.example.demo.auth.util.JwtUtil;
import com.example.demo.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private AuthRepository authRepository;

    public Map<String, Object> register(String phone, String password, String confirmPassword, String code, Boolean agreeTerms) {
        // 验证密码确认
        if (!password.equals(confirmPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "密码和确认密码不一致");
        }

        // 验证是否同意条款
        if (!agreeTerms) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请同意用户协议");
        }

        // 检查手机号是否已存在
        Optional<Member> existingMember = authRepository.findByPhone(phone);
        if (existingMember.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "注册失败，手机号已存在");
        }

        // 创建新会员
        Member member = new Member();
        member.setPhone(phone);
        member.setPassword(password); // 注意：实际项目中应该加密存储
        member.setNickname("用户" + phone);
        member.setStatus("正常");
        member.setRegisterTime(LocalDate.now());
        member.setCreatedAt(LocalDateTime.now());

        Member savedMember = authRepository.save(member);

        // 返回注册结果
        Map<String, Object> result = new HashMap<>();
        result.put("id", savedMember.getId());
        result.put("phone", savedMember.getPhone());
        result.put("nickname", savedMember.getNickname());
        result.put("status", savedMember.getStatus());
        result.put("createdAt", savedMember.getCreatedAt());

        return result;
    }

    public Map<String, Object> login(String phone, String password, Boolean rememberMe) {
        Optional<Member> memberOptional = authRepository.findByPhone(phone);
        
        if (memberOptional.isEmpty() || !memberOptional.get().getPassword().equals(password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "手机号或密码错误");
        }

        Member member = memberOptional.get();

        // 生成token
        String token = JwtUtil.generateToken(phone);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        
        Map<String, Object> memberInfo = new HashMap<>();
        memberInfo.put("id", member.getId());
        memberInfo.put("phone", member.getPhone());
        memberInfo.put("nickname", member.getNickname());
        memberInfo.put("status", member.getStatus());
        
        result.put("member", memberInfo);

        return result;
    }

    public Map<String, Object> getCurrentUser(String phone) {
        Optional<Member> memberOptional = authRepository.findByPhone(phone);
        
        if (memberOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户不存在");
        }

        Member member = memberOptional.get();
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", member.getId());
        result.put("phone", member.getPhone());
        result.put("nickname", member.getNickname());
        result.put("status", member.getStatus());
        result.put("createdAt", member.getCreatedAt());

        return result;
    }
}