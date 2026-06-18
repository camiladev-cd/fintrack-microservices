package com.fintrack.transaction.controller;

import com.fintrack.transaction.dto.TransactionDTO;
import com.fintrack.transaction.model.TransactionType;
import com.fintrack.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Transaction management with Kafka events")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Create a new transaction and publish Kafka event")
    public ResponseEntity<TransactionDTO.TransactionResponse> createTransaction(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody TransactionDTO.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.createTransaction(userId, request));
    }

    @GetMapping("/{transactionId}")
    @Operation(summary = "Get transaction by ID")
    public ResponseEntity<TransactionDTO.TransactionResponse> getTransaction(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long transactionId) {
        return ResponseEntity.ok(transactionService.getTransaction(userId, transactionId));
    }

    @GetMapping
    @Operation(summary = "Get all transactions for current user (paginated)")
    public ResponseEntity<TransactionDTO.PageResponse> getMyTransactions(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(transactionService.getMyTransactions(userId, page, size));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get transactions filtered by type")
    public ResponseEntity<TransactionDTO.PageResponse> getByType(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable TransactionType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                transactionService.getMyTransactionsByType(userId, type, page, size));
    }
}