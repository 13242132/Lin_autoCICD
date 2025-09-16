package com.example.demo.service;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;


import com.example.demo.entity.CheckIn;
import com.example.demo.repository.CheckInRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CheckInService {
    private final CheckInRepository repository;

    public CheckInService(CheckInRepository repository) {
        this.repository = repository;
    }

    // 创建签到记录
    public CheckIn createCheckIn(CheckIn checkIn) {
        // 检查是否已签到（单表查询）
        Optional<CheckIn> existing = repository.findByCourseIdAndMemberId(checkIn.getCourseId(), checkIn.getMemberId());
        if (existing.isPresent()) {
            throw new RuntimeException("CHECKIN_FAILED"); // 模拟已签到错误
        }
        // 忽略未预约检查，直接设置状态并保存
        checkIn.setStatus("已签到");
        return repository.insert(checkIn);
    }
}
