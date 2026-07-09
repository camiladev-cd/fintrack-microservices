package com.fintrack.payment.service;

import com.fintrack.payment.kafka.UserCreatedEvent;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomerSyncService {

    public String createStripeCustomer(UserCreatedEvent event) {
        try {
            CustomerCreateParams params = CustomerCreateParams.builder()
                    .setEmail(event.getEmail())
                    .setName(event.getFullName())
                    .putMetadata("userId", event.getUserId().toString())
                    .putMetadata("source", "fintrack")
                    .build();

            Customer customer = Customer.create(params);

            log.info("Stripe customer created: {} for userId: {}",
                    customer.getId(), event.getUserId());

            return customer.getId();

        } catch (StripeException e) {
            log.error("Failed to create Stripe customer for userId: {}",
                    event.getUserId(), e);
            throw new RuntimeException("Failed to create Stripe customer", e);
        }
    }
}