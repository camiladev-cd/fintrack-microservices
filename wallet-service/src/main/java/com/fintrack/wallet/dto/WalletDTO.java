package com.fintrack.wallet.dto;

import com.fintrack.wallet.model.WalletType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class WalletDTO {

    public record CreateRequest(
            @NotBlank(message = "Wallet name is required")
            String name,

            @NotNull(message = "Wallet type is required")
            WalletType type,

            @NotNull(message = "Initial balance is required")
            @DecimalMin(value = "0.0", message = "Initial balance must be non-negative")
            BigDecimal initialBalance
    ) {}

    public record DepositRequest(
            @NotNull(message = "Amount is required")
            @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
            BigDecimal amount
    ) {}

    public record WithdrawRequest(
            @NotNull(message = "Amount is required")
            @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
            BigDecimal amount
    ) {}

    public record WalletResponse(
            Long id,
            Long userId,
            String name,
            WalletType type,
            BigDecimal balance,
            boolean active,
            Long version
    ) {}
}