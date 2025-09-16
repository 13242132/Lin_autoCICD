package com.example.demo.repository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import jakarta.persistence.*;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.entity.MembershipCard;

@Repository
public interface MembershipCardRepository extends JpaRepository<MembershipCard, Long> {
    Optional<MembershipCard> findByCardNumber(String cardNumber);
}
