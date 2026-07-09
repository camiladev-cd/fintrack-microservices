package com.fintrack.payment.repository;

import com.fintrack.payment.model.Payment;
import com.fintrack.payment.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    Optional<Payment> findByPaymentIntentId(String paymentIntentId);

    List<Payment> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Payment> findByStatus(PaymentStatus status);
}