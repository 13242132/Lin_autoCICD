package com.example.demo.api.repository;

import com.example.demo.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeApiRepository extends JpaRepository<Like, Long>, JpaSpecificationExecutor<Like> {
}
