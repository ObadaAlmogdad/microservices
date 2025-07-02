package com.example.payment_service.service;

import com.example.payment_service.client.UserClient;
import com.example.payment_service.dto.ApiResponse;
import com.example.payment_service.dto.UserDto;
import com.example.payment_service.model.Transaction;
import com.example.payment_service.model.TransactionType;
import com.example.payment_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final TransactionRepository transactionRepository;
    private final UserClient userClient;

    @Transactional
    public Transaction processPayment(Long userId, double amount, String description, String authorization) {
        ApiResponse<UserDto> response = userClient.getUserById(userId, authorization);
        UserDto user = (response != null) ? response.getData() : null;
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User not found");
        }
        Transaction tx = Transaction.builder()
                .userId(userId)
                .type(TransactionType.PAYMENT)
                .amount(amount)
                .description(description)
                .build();
        Transaction saved = transactionRepository.save(tx);
        userClient.updateWallet(userId, -amount, authorization);
        return saved;
    }

    @Transactional
    public Transaction processRefund(Long userId, double amount, String description, String authorization) {
        ApiResponse<UserDto> response = userClient.getUserById(userId, authorization);
        UserDto user = (response != null) ? response.getData() : null;
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User not found");
        }
        Transaction tx = Transaction.builder()
                .userId(userId)
                .type(TransactionType.REFUND)
                .amount(amount)
                .description(description)
                .build();
        Transaction saved = transactionRepository.save(tx);
        userClient.updateWallet(userId, amount, authorization);
        return saved;
    }

    @Transactional
    public Transaction processCharge(Long userId, double amount, String description, String authorization) {
        ApiResponse<UserDto> response = userClient.getUserById(userId, authorization);
        UserDto user = (response != null) ? response.getData() : null;
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User not found");
        }
        Transaction tx = Transaction.builder()
                .userId(userId)
                .type(TransactionType.CHARGE)
                .amount(amount)
                .description(description)
                .build();
        Transaction saved = transactionRepository.save(tx);
        userClient.updateWallet(userId, -amount, authorization);
        return saved;
    }

    public List<Transaction> getUserTransactions(Long userId) {
        return transactionRepository.findByUserId(userId);
    }
} 