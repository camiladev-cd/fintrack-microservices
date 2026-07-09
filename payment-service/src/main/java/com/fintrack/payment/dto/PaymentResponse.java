package com.fintrack.payment.dto;

import com.fintrack.payment.model.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class PaymentResponse {
    private UUID id;
    private String paymentIntentId;
    private Long amount;
    private String currency;
    private PaymentStatus status;
    private Instant createdAt;
}