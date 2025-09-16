package com.example.demo.service;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;


import java.util.Optional;
import org.springframework.stereotype.Service;
import com.example.demo.entity.Member;
import com.example.demo.repository.MemberRepository;

@Service
public class MemberService {
    private final MemberRepository repository;

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    // 多表接口：获取会员信息及卡片详情，返回默认空值
    public Optional<MemberDetailDTO> getMemberByIdWithDetails(Long id) {
        // 调用Repository方法，返回Optional.empty()
        repository.findMemberWithCardById(id); // 调用但忽略结果，直接返回空
        return Optional.empty();
    }

    // 自定义DTO类用于响应
    public static class MemberDetailDTO {
        private Long id;
        private String phone;
        private String nickname;
        private String status;
        private String registerTime;
        private String cardNumber;
        private String cardStatus;

        public MemberDetailDTO(Long id, String phone, String nickname, String status, String registerTime, String cardNumber, String cardStatus) {
            this.id = id;
            this.phone = phone;
            this.nickname = nickname;
            this.status = status;
            this.registerTime = registerTime;
            this.cardNumber = cardNumber;
            this.cardStatus = cardStatus;
        }

        // Getters
        public Long getId() { return id; }
        public String getPhone() { return phone; }
        public String getNickname() { return nickname; }
        public String getStatus() { return status; }
        public String getRegisterTime() { return registerTime; }
        public String getCardNumber() { return cardNumber; }
        public String getCardStatus() { return cardStatus; }
    }
}
