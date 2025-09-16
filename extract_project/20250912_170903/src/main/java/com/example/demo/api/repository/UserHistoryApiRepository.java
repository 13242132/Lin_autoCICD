package com.example.demo.api.repository;

import com.example.demo.entity.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserHistoryApiRepository extends JpaRepository<UserHistory, Long>, JpaSpecificationExecutor<UserHistory> {
}
