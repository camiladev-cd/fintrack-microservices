package com.fintrack.payment.kafka;

import com.fintrack.payment.service.CustomerSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCreatedListener {

    private final CustomerSyncService customerSyncService;

    @KafkaListener(
            topics = "user.created",
            groupId = "payment-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleUserCreated(UserCreatedEvent event) {
        log.info("Received user.created event for userId: {}", event.getUserId());
        try {
            customerSyncService.createStripeCustomer(event);
        } catch (Exception e) {
            log.error("Failed to create Stripe customer for userId: {}",
                    event.getUserId(), e);
        }
    }
}