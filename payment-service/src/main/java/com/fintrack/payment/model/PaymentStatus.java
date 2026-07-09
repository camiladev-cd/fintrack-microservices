package com.fintrack.payment.model;

public enum PaymentStatus {
    PENDING,
    PROCESSING,
    SUCCEEDED,
    FAILED,
    CANCELED,
    REFUNDED,
    PARTIALLY_REFUNDED
}