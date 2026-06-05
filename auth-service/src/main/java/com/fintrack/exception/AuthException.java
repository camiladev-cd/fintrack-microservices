package com.fintrack.auth.exception;

public class AuthException {

    public static class EmailAlreadyExistsException extends RuntimeException {
        public EmailAlreadyExistsException(String email) {
            super("Email already registered: " + email);
        }
    }

    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException() {
            super("Invalid email or password");
        }
    }

    public static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException() {
            super("Token is invalid or expired");
        }
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String email) {
            super("User not found: " + email);
        }
    }
}