package com.example.demo.service;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.demo.entity.Course;
import com.example.demo.repository.CourseRepository;

@Service
public class CourseService {
    
    private final CourseRepository repository;
    
    public CourseService(CourseRepository repository) {
        this.repository = repository;
    }
    
    // 调用数据库层：根据日期和关键词查询课程列表
    public List<Course> getCourses(String dateStr, String keyword) {
        LocalDate date = null;
        if (dateStr != null && !dateStr.isEmpty()) {
            date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        }
        return repository.findCoursesByDateAndKeyword(date, keyword);
    }
}
