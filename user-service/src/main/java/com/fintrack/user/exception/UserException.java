package com.fintrack.user.exception;

public class UserException {

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(Long id) {
            super("User profile not found for id: " + id);
        }
    }

    public static class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException(String email) {
            super("User profile already exists for email: " + email);
        }
    }
}