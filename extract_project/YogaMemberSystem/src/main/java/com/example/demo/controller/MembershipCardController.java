package com.example.demo.controller;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.service.MembershipCardService;

@RestController
@RequestMapping("/api/membership-cards")
public class MembershipCardController {

    private final MembershipCardService service;

    public MembershipCardController(MembershipCardService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> listMembershipCards(@RequestParam(required = false) String keyword) {
        List<MembershipCardService.MembershipCardListItem> list = service.listMembershipCards(keyword);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{cardNumber}/status")
    public ResponseEntity<?> updateMembershipCardStatus(@PathVariable String cardNumber, @RequestBody String status) {
        MembershipCardService.MembershipCardStatusUpdateResponse response = service.updateMembershipCardStatus(cardNumber, status);
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("STATUS_UPDATE_FAILED", "状态更新失败，无效的状态值"));
        }
    }

    private static class ErrorResponse {
        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() { return error; }
        public String getMessage() { return message; }
    }
}
