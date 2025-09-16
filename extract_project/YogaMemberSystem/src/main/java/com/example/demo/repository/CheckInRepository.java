package com.example.demo.repository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import jakarta.persistence.*;


import com.example.demo.entity.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    // 单表：根据课程ID和会员ID查询签到记录
    Optional<CheckIn> findByCourseIdAndMemberId(Long courseId, Long memberId);
    
    // 单表：插入签到记录
    default CheckIn insert(CheckIn entity) {
        return save(entity);
    }
}
