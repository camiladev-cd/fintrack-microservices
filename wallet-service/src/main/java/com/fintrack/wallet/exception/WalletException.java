package com.fintrack.wallet.exception;

public class WalletException {

    public static class WalletNotFoundException extends RuntimeException {
        public WalletNotFoundException(Long id) {
            super("Wallet not found for id: " + id);
        }
    }

    public static class InsufficientFundsException extends RuntimeException {
        public InsufficientFundsException(Long walletId) {
            super("Insufficient funds in wallet: " + walletId);
        }
    }

    public static class WalletNotOwnedException extends RuntimeException {
        public WalletNotOwnedException(Long walletId, Long userId) {
            super("Wallet " + walletId + " does not belong to user " + userId);
        }
    }

    public static class OptimisticLockException extends RuntimeException {
        public OptimisticLockException() {
            super("Wallet was modified by another transaction. Please retry.");
        }
    }
}