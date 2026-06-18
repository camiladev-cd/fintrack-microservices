package com.fintrack.transaction.service;

import com.fintrack.transaction.dto.TransactionDTO;
import com.fintrack.transaction.exception.TransactionException;
import com.fintrack.transaction.kafka.TransactionEvent;
import com.fintrack.transaction.kafka.TransactionProducer;
import com.fintrack.transaction.model.Transaction;
import com.fintrack.transaction.model.TransactionStatus;
import com.fintrack.transaction.model.TransactionType;
import com.fintrack.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionProducer transactionProducer;

    @Transactional
    public TransactionDTO.TransactionResponse createTransaction(
            Long userId, TransactionDTO.CreateRequest request) {

        Transaction transaction = Transaction.builder()
                .userId(userId)
                .walletId(request.walletId())
                .amount(request.amount())
                .type(request.type())
                .description(request.description())
                .category(request.category())
                .status(TransactionStatus.PENDING)
                .build();

        transactionRepository.save(transaction);
        log.info("Transaction created — id={}, userId={}, type={}",
                transaction.getId(), userId, request.type());

        TransactionEvent event = new TransactionEvent(
                transaction.getId(),
                userId,
                request.walletId(),
                request.amount(),
                request.type(),
                request.description(),
                request.category(),
                transaction.getCreatedAt()
        );

        transactionProducer.publishTransactionCreated(event);

        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setUpdatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        return toResponse(transaction);
    }

    public TransactionDTO.TransactionResponse getTransaction(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionException.TransactionNotFoundException(transactionId));

        if (!transaction.getUserId().equals(userId)) {
            throw new TransactionException.UnauthorizedTransactionException(transactionId, userId);
        }

        return toResponse(transaction);
    }

    public TransactionDTO.PageResponse getMyTransactions(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Transaction> result = transactionRepository.findByUserId(userId, pageable);
        return toPageResponse(result);
    }

    public TransactionDTO.PageResponse getMyTransactionsByType(
            Long userId, TransactionType type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Transaction> result = transactionRepository.findByUserIdAndType(userId, type, pageable);
        return toPageResponse(result);
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private TransactionDTO.TransactionResponse toResponse(Transaction t) {
        return new TransactionDTO.TransactionResponse(
                t.getId(),
                t.getUserId(),
                t.getWalletId(),
                t.getAmount(),
                t.getType(),
                t.getStatus(),
                t.getDescription(),
                t.getCategory(),
                t.getCreatedAt()
        );
    }

    private TransactionDTO.PageResponse toPageResponse(Page<Transaction> page) {
        return new TransactionDTO.PageResponse(
                page.getContent().stream().map(this::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}