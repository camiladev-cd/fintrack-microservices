package com.fintrack.transaction.dto;

import com.fintrack.transaction.model.TransactionStatus;
import com.fintrack.transaction.model.TransactionType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDTO {

    public record CreateRequest(
            @NotNull(message = "Wallet ID is required")
            Long walletId,

            @NotNull(message = "Amount is required")
            @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
            BigDecimal amount,

            @NotNull(message = "Transaction type is required")
            TransactionType type,

            @NotBlank(message = "Description is required")
            @Size(max = 255, message = "Description too long")
            String description,

            @Size(max = 100, message = "Category too long")
            String category
    ) {}

    public record TransactionResponse(
            Long id,
            Long userId,
            Long walletId,
            BigDecimal amount,
            TransactionType type,
            TransactionStatus status,
            String description,
            String category,
            LocalDateTime createdAt
    ) {}

    public record PageResponse(
            java.util.List<TransactionResponse> content,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {}
}