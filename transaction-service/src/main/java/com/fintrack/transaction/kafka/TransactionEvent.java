package com.fintrack.transaction.kafka;

import com.fintrack.transaction.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionEvent(
        Long transactionId,
        Long userId,
        Long walletId,
        BigDecimal amount,
        TransactionType type,
        String description,
        String category,
        LocalDateTime createdAt
) {}