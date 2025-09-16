package com.example.demo.service;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;


import java.util.List;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import com.example.demo.entity.Appointment;
import com.example.demo.repository.AppointmentRepository;

@Service
public class AppointmentService {
    private final AppointmentRepository repository;

    public AppointmentService(AppointmentRepository repository) {
        this.repository = repository;
    }

    // 单表：创建预约
    public Appointment createAppointment(Appointment appointment) {
        appointment.setStatus("已预约");
        appointment.setCreatedAt(LocalDateTime.now());
        return repository.save(appointment);
    }

    // 多表接口：获取我的预约，返回默认值
    public List<Appointment> getMyAppointments(String status) {
        return repository.findMyAppointments(status);
    }

    // 单表：取消预约
    public void cancelAppointment(Long id) {
        repository.deleteById(id);
    }
}
