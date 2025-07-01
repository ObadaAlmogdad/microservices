package com.example.course_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.ResponseEntity;

@FeignClient(name = "payment-service")
public interface PaymentClient {
    @PostMapping("/payments/pay")
    ResponseEntity<?> pay(@RequestParam("userId") Long userId,
                         @RequestParam("amount") double amount,
                         @RequestParam("description") String description,
                         @RequestHeader("Authorization") String authorization);
} 