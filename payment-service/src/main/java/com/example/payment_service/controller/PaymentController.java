package com.example.payment_service.controller;

import com.example.payment_service.model.Transaction;
import com.example.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<Transaction> pay(@RequestParam Long userId, @RequestParam double amount, @RequestParam String description, @RequestHeader("Authorization") String authorization) {
        Transaction tx = paymentService.processPayment(userId, amount, description, authorization);
        return ResponseEntity.ok(tx);
    }

    @PostMapping("/refund")
    public ResponseEntity<Transaction> refund(@RequestParam Long userId, @RequestParam double amount, @RequestParam String description, @RequestHeader("Authorization") String authorization) {
        Transaction tx = paymentService.processRefund(userId, amount, description, authorization);
        return ResponseEntity.ok(tx);
    }

    @PostMapping("/charge")
    public ResponseEntity<Transaction> charge(@RequestParam Long userId, @RequestParam double amount, @RequestParam String description, @RequestHeader("Authorization") String authorization) {
        Transaction tx = paymentService.processCharge(userId, amount, description, authorization);
        return ResponseEntity.ok(tx);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Transaction>> getUserTransactions(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.getUserTransactions(userId));
    }
} 