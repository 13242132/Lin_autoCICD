package com.example.demo.api.repository;

import com.example.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentApiRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
}
