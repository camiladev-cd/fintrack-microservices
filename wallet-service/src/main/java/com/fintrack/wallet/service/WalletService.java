package com.fintrack.wallet.service;

import com.fintrack.wallet.dto.WalletDTO;
import com.fintrack.wallet.exception.WalletException;
import com.fintrack.wallet.model.Wallet;
import com.fintrack.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;

    @Transactional
    public WalletDTO.WalletResponse createWallet(Long userId, WalletDTO.CreateRequest request) {
        Wallet wallet = Wallet.builder()
                .userId(userId)
                .name(request.name())
                .type(request.type())
                .balance(request.initialBalance())
                .build();

        walletRepository.save(wallet);
        log.info("Wallet created for userId={}, walletId={}", userId, wallet.getId());
        return toResponse(wallet);
    }

    public List<WalletDTO.WalletResponse> getMyWallets(Long userId) {
        return walletRepository.findByUserIdAndActiveTrue(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public WalletDTO.WalletResponse getWallet(Long userId, Long walletId) {
        Wallet wallet = walletRepository.findByIdAndUserId(walletId, userId)
                .orElseThrow(() -> new WalletException.WalletNotFoundException(walletId));
        return toResponse(wallet);
    }

    @Transactional
    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public WalletDTO.WalletResponse deposit(Long userId, Long walletId,
                                            WalletDTO.DepositRequest request) {
        Wallet wallet = walletRepository.findByIdWithLock(walletId)
                .orElseThrow(() -> new WalletException.WalletNotFoundException(walletId));

        if (!wallet.getUserId().equals(userId)) {
            throw new WalletException.WalletNotOwnedException(walletId, userId);
        }

        wallet.setBalance(wallet.getBalance().add(request.amount()));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        log.info("Deposit of {} to walletId={}", request.amount(), walletId);
        return toResponse(wallet);
    }

    @Transactional
    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    public WalletDTO.WalletResponse withdraw(Long userId, Long walletId,
                                             WalletDTO.WithdrawRequest request) {
        Wallet wallet = walletRepository.findByIdWithLock(walletId)
                .orElseThrow(() -> new WalletException.WalletNotFoundException(walletId));

        if (!wallet.getUserId().equals(userId)) {
            throw new WalletException.WalletNotOwnedException(walletId, userId);
        }

        if (wallet.getBalance().compareTo(request.amount()) < 0) {
            throw new WalletException.InsufficientFundsException(walletId);
        }

        wallet.setBalance(wallet.getBalance().subtract(request.amount()));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        log.info("Withdrawal of {} from walletId={}", request.amount(), walletId);
        return toResponse(wallet);
    }

    @Transactional
    public void deactivateWallet(Long userId, Long walletId) {
        Wallet wallet = walletRepository.findByIdAndUserId(walletId, userId)
                .orElseThrow(() -> new WalletException.WalletNotFoundException(walletId));
        wallet.setActive(false);
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);
        log.info("Wallet deactivated walletId={}", walletId);
    }

    private WalletDTO.WalletResponse toResponse(Wallet wallet) {
        return new WalletDTO.WalletResponse(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getName(),
                wallet.getType(),
                wallet.getBalance(),
                wallet.isActive(),
                wallet.getVersion()
        );
    }
}