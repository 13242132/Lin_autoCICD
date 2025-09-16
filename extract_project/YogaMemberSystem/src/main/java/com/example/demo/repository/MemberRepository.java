package com.example.demo.repository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import jakarta.persistence.*;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 多表接口：获取会员信息及卡片详情，返回默认空值
    default Optional<Member> findMemberWithCardById(Long id) {
        return Optional.empty();
    }
}
