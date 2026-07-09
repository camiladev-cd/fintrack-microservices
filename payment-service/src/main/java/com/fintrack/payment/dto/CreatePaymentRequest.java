package com.fintrack.payment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CreatePaymentRequest {

    @NotNull
    private UUID userId;

    @NotNull
    @Min(50)
    private Long amount;

    @NotBlank
    @Size(min = 3, max = 3)
    private String currency;

    @NotBlank
    private String idempotencyKey;

    private String description;
}