package com.fintrack.auth.service;

import com.fintrack.auth.model.Role;
import com.fintrack.auth.model.User;
import com.fintrack.auth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret",
                "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "expiration", 900000L);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 604800000L);

        testUser = User.builder()
                .id(1L)
                .firstName("Camila")
                .lastName("Duenas")
                .email("camila@fintrack.com")
                .password("encoded_password")
                .role(Role.USER)
                .build();
    }

    @Test
    @DisplayName("Should generate valid access token")
    void shouldGenerateValidAccessToken() {
        String token = jwtService.generateAccessToken(testUser, testUser.getId());
        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("Should extract email from token")
    void shouldExtractEmailFromToken() {
        String token = jwtService.generateAccessToken(testUser, testUser.getId());
        String email = jwtService.extractEmail(token);
        assertThat(email).isEqualTo("camila@fintrack.com");
    }

    @Test
    @DisplayName("Should extract userId from token")
    void shouldExtractUserIdFromToken() {
        String token = jwtService.generateAccessToken(testUser, testUser.getId());
        Long userId = jwtService.extractUserId(token);
        assertThat(userId).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should extract role from token")
    void shouldExtractRoleFromToken() {
        String token = jwtService.generateAccessToken(testUser, testUser.getId());
        String role = jwtService.extractRole(token);
        assertThat(role).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("Should validate token successfully")
    void shouldValidateTokenSuccessfully() {
        String token = jwtService.generateAccessToken(testUser, testUser.getId());
        boolean valid = jwtService.isTokenValid(token, testUser);
        assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("Should reject token with wrong email")
    void shouldRejectTokenWithWrongEmail() {
        String token = jwtService.generateAccessToken(testUser, testUser.getId());
        User otherUser = User.builder()
                .email("other@fintrack.com")
                .password("pass")
                .role(Role.USER)
                .build();
        boolean valid = jwtService.isTokenValid(token, otherUser);
        assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("Should generate different access and refresh tokens")
    void shouldGenerateDifferentTokens() {
        String accessToken  = jwtService.generateAccessToken(testUser, testUser.getId());
        String refreshToken = jwtService.generateRefreshToken(testUser);
        assertThat(accessToken).isNotEqualTo(refreshToken);
    }
}
