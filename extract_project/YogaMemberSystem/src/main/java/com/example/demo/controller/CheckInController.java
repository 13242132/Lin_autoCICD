package com.example.demo.controller;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


import com.example.demo.entity.CheckIn;
import com.example.demo.service.CheckInService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkins")
public class CheckInController {
    private final CheckInService service;

    public CheckInController(CheckInService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> createCheckIn(@RequestBody CheckIn checkIn) {
        try {
            CheckIn created = service.createCheckIn(checkIn);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            if ("CHECKIN_FAILED".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("CHECKIN_FAILED", "签到失败，未预约或已签到"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponse("INTERNAL_ERROR", e.getMessage()));
            }
        }
    }

    // 错误响应类
    private static class ErrorResponse {
        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }
    }
}
