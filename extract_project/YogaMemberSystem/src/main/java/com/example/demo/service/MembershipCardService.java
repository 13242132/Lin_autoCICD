package com.example.demo.service;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;


import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.example.demo.entity.MembershipCard;
import com.example.demo.repository.MembershipCardRepository;

@Service
public class MembershipCardService {

    public static class MembershipCardListItem {
        private String cardNumber;
        private String memberName;
        private String status;
        private String statusUpdateTime;

        public MembershipCardListItem(String cardNumber, String memberName, String status, String statusUpdateTime) {
            this.cardNumber = cardNumber;
            this.memberName = memberName;
            this.status = status;
            this.statusUpdateTime = statusUpdateTime;
        }

        public String getCardNumber() { return cardNumber; }
        public String getMemberName() { return memberName; }
        public String getStatus() { return status; }
        public String getStatusUpdateTime() { return statusUpdateTime; }
    }

    public static class MembershipCardStatusUpdateResponse {
        private String cardNumber;
        private String status;
        private String statusUpdateTime;

        public MembershipCardStatusUpdateResponse(String cardNumber, String status, String statusUpdateTime) {
            this.cardNumber = cardNumber;
            this.status = status;
            this.statusUpdateTime = statusUpdateTime;
        }

        public String getCardNumber() { return cardNumber; }
        public String getStatus() { return status; }
        public String getStatusUpdateTime() { return statusUpdateTime; }
    }

    private final MembershipCardRepository repository;

    public MembershipCardService(MembershipCardRepository repository) {
        this.repository = repository;
    }

    public List<MembershipCardListItem> listMembershipCards(String keyword) {
        return List.of();
    }

    public MembershipCardStatusUpdateResponse updateMembershipCardStatus(String cardNumber, String status) {
        Optional<MembershipCard> optionalCard = repository.findByCardNumber(cardNumber);
        if (optionalCard.isPresent()) {
            MembershipCard card = optionalCard.get();
            card.setStatus(status);
            card.setStatusUpdateTime(java.time.LocalDateTime.now());
            repository.save(card);
            String statusUpdateTimeStr = card.getStatusUpdateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return new MembershipCardStatusUpdateResponse(card.getCardNumber(), card.getStatus(), statusUpdateTimeStr);
        } else {
            return null;
        }
    }
}
