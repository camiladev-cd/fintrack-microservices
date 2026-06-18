package com.fintrack.wallet.controller;

import com.fintrack.wallet.dto.WalletDTO;
import com.fintrack.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
@Tag(name = "Wallets", description = "Wallet management with optimistic locking")
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    @Operation(summary = "Create a new wallet")
    public ResponseEntity<WalletDTO.WalletResponse> createWallet(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody WalletDTO.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(walletService.createWallet(userId, request));
    }

    @GetMapping
    @Operation(summary = "Get all wallets for current user")
    public ResponseEntity<List<WalletDTO.WalletResponse>> getMyWallets(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(walletService.getMyWallets(userId));
    }

    @GetMapping("/{walletId}")
    @Operation(summary = "Get wallet by ID")
    public ResponseEntity<WalletDTO.WalletResponse> getWallet(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long walletId) {
        return ResponseEntity.ok(walletService.getWallet(userId, walletId));
    }

    @PostMapping("/{walletId}/deposit")
    @Operation(summary = "Deposit amount into wallet")
    public ResponseEntity<WalletDTO.WalletResponse> deposit(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long walletId,
            @Valid @RequestBody WalletDTO.DepositRequest request) {
        return ResponseEntity.ok(walletService.deposit(userId, walletId, request));
    }

    @PostMapping("/{walletId}/withdraw")
    @Operation(summary = "Withdraw amount from wallet")
    public ResponseEntity<WalletDTO.WalletResponse> withdraw(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long walletId,
            @Valid @RequestBody WalletDTO.WithdrawRequest request) {
        return ResponseEntity.ok(walletService.withdraw(userId, walletId, request));
    }

    @DeleteMapping("/{walletId}")
    @Operation(summary = "Deactivate wallet")
    public ResponseEntity<Void> deactivateWallet(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long walletId) {
        walletService.deactivateWallet(userId, walletId);
        return ResponseEntity.noContent().build();
    }
}