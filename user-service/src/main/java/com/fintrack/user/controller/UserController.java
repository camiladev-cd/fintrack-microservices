package com.fintrack.user.controller;

import com.fintrack.user.dto.UserDTO;
import com.fintrack.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile management")
public class UserController {

    private final UserService userService;

    @PostMapping("/profile")
    @Operation(summary = "Create user profile (called internally after register)")
    public ResponseEntity<UserDTO.UserResponse> createProfile(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-FirstName") String firstName,
            @RequestHeader("X-User-LastName") String lastName) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createProfile(userId, email, firstName, lastName));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserDTO.UserResponse> getMyProfile(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<UserDTO.UserResponse> updateMyProfile(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UserDTO.UpdateRequest request) {
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }

    @DeleteMapping("/me")
    @Operation(summary = "Deactivate current user profile")
    public ResponseEntity<Void> deactivateMyProfile(
            @RequestHeader("X-User-Id") Long userId) {
        userService.deactivateProfile(userId);
        return ResponseEntity.noContent().build();
    }
}