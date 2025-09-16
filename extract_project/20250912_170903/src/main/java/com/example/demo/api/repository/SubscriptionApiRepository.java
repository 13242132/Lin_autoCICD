package com.example.demo.api.repository;

import com.example.demo.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionApiRepository extends JpaRepository<Subscription, Long>, JpaSpecificationExecutor<Subscription> {
}
