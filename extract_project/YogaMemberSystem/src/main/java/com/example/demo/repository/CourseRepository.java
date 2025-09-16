package com.example.demo.repository;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import jakarta.persistence.*;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.demo.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    // 单表：根据日期和关键词查询课程列表
    @Query("SELECT c FROM Course c WHERE " +
           "(:date IS NULL OR CAST(c.startTime AS localdate) = :date) AND " +
           "(:keyword IS NULL OR c.name LIKE %:keyword% OR c.coach LIKE %:keyword%)")
    List<Course> findCoursesByDateAndKeyword(@Param("date") LocalDate date, 
                                            @Param("keyword") String keyword);
    
    // 单表：获取所有课程
    default List<Course> findAllCourses() {
        return findAll();
    }
}
