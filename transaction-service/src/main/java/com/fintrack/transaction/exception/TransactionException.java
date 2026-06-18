package com.fintrack.transaction.exception;

public class TransactionException {

    public static class TransactionNotFoundException extends RuntimeException {
        public TransactionNotFoundException(Long id) {
            super("Transaction not found for id: " + id);
        }
    }

    public static class InvalidTransactionException extends RuntimeException {
        public InvalidTransactionException(String message) {
            super(message);
        }
    }

    public static class UnauthorizedTransactionException extends RuntimeException {
        public UnauthorizedTransactionException(Long transactionId, Long userId) {
            super("Transaction " + transactionId + " does not belong to user " + userId);
        }
    }
}