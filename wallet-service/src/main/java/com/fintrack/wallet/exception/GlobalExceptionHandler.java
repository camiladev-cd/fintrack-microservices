package com.fintrack.wallet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ErrorResponse(
            int status,
            String error,
            String message,
            LocalDateTime timestamp
    ) {}

    public record ValidationErrorResponse(
            int status,
            String error,
            Map<String, String> fieldErrors,
            LocalDateTime timestamp
    ) {}

    @ExceptionHandler(WalletException.WalletNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            WalletException.WalletNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, "Not Found", ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(WalletException.InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(
            WalletException.InsufficientFundsException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse(422, "Unprocessable Entity", ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(WalletException.WalletNotOwnedException.class)
    public ResponseEntity<ErrorResponse> handleNotOwned(
            WalletException.WalletNotOwnedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(403, "Forbidden", ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler({ObjectOptimisticLockingFailureException.class,
            WalletException.OptimisticLockException.class})
    public ResponseEntity<ErrorResponse> handleOptimisticLock(Exception ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(409, "Conflict",
                        "Wallet was modified by another transaction. Please retry.",
                        LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            fieldErrors.put(field, error.getDefaultMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationErrorResponse(400, "Validation Failed", fieldErrors, LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, "Internal Server Error",
                        "An unexpected error occurred", LocalDateTime.now()));
    }
}