package com.fintrack.payment.controller;

import com.fintrack.payment.dto.CreatePaymentRequest;
import com.fintrack.payment.dto.PaymentResponse;
import com.fintrack.payment.service.PaymentIntentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Validated
public class PaymentController {

    private final PaymentIntentService paymentIntentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestBody @Valid CreatePaymentRequest request) {
        PaymentResponse response = paymentIntentService.createPaymentIntent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable UUID paymentId) {
        return ResponseEntity.ok(paymentIntentService.getPayment(paymentId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponse>> getUserPayments(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(paymentIntentService.getUserPayments(userId));
    }
}