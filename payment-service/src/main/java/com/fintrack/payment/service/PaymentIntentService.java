package com.fintrack.payment.service;

import com.fintrack.payment.dto.CreatePaymentRequest;
import com.fintrack.payment.dto.PaymentResponse;
import com.fintrack.payment.model.Payment;
import com.fintrack.payment.model.PaymentStatus;
import com.fintrack.payment.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.net.RequestOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentIntentService {

    private final PaymentRepository paymentRepository;

    public PaymentResponse createPaymentIntent(CreatePaymentRequest request) {

        // 1. Idempotencia: si ya existe este key, devuelve el pago existente
        Optional<Payment> existing = paymentRepository
                .findByIdempotencyKey(request.getIdempotencyKey());
        if (existing.isPresent()) {
            log.info("Idempotent request detected for key: {}",
                    request.getIdempotencyKey());
            return toResponse(existing.get());
        }

        try {
            // 2. Crear PaymentIntent en Stripe
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(request.getAmount())
                    .setCurrency(request.getCurrency())
                    .setDescription(request.getDescription())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .build();

            RequestOptions options = RequestOptions.builder()
                    .setIdempotencyKey(request.getIdempotencyKey())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params, options);

            // 3. Persistir en nuestra DB
            Payment payment = Payment.builder()
                    .userId(request.getUserId())
                    .paymentIntentId(intent.getId())
                    .idempotencyKey(request.getIdempotencyKey())
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .status(PaymentStatus.PENDING)
                    .description(request.getDescription())
                    .build();

            paymentRepository.save(payment);

            log.info("PaymentIntent created: {} for user: {}",
                    intent.getId(), request.getUserId());

            return toResponse(payment);

        } catch (StripeException e) {
            log.error("Stripe error creating PaymentIntent: {}", e.getMessage());
            throw new RuntimeException("Failed to create payment: " + e.getMessage());
        }
    }

    public PaymentResponse getPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
        return toResponse(payment);
    }

    public java.util.List<PaymentResponse> getUserPayments(UUID userId) {
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .paymentIntentId(payment.getPaymentIntentId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}