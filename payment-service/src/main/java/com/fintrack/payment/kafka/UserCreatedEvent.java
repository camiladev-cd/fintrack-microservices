package com.fintrack.payment.kafka;

import lombok.Data;

import java.util.UUID;

@Data
public class UserCreatedEvent {
    private UUID userId;
    private String email;
    private String fullName;
}