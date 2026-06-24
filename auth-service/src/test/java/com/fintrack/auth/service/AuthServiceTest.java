package com.fintrack.auth.service;

import com.fintrack.auth.dto.AuthDTO;
import com.fintrack.auth.exception.AuthException;
import com.fintrack.auth.model.Role;
import com.fintrack.auth.model.User;
import com.fintrack.auth.repository.UserRepository;
import com.fintrack.auth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private RedisTemplate<String, String> redisTemplate;
    @Mock private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
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
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() {
        when(userRepository.existsByEmail("camila@fintrack.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded_password");
        when(userRepository.save(any())).thenReturn(testUser);
        when(jwtService.generateAccessToken(any(), any())).thenReturn("access_token");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh_token");
        when(jwtService.getExpiration()).thenReturn(900000L);

        AuthDTO.AuthResponse response = authService.register(
                new AuthDTO.RegisterRequest("Camila", "Duenas",
                        "camila@fintrack.com", "12345678"));

        assertThat(response.accessToken()).isEqualTo("access_token");
        assertThat(response.refreshToken()).isEqualTo("refresh_token");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("camila@fintrack.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(
                new AuthDTO.RegisterRequest("Camila", "Duenas",
                        "camila@fintrack.com", "12345678")))
                .isInstanceOf(AuthException.EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should login successfully")
    void shouldLoginSuccessfully() {
        when(userRepository.findByEmail("camila@fintrack.com")).thenReturn(Optional.of(testUser));
        when(jwtService.generateAccessToken(any(), any())).thenReturn("access_token");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh_token");
        when(jwtService.getExpiration()).thenReturn(900000L);

        AuthDTO.AuthResponse response = authService.login(
                new AuthDTO.LoginRequest("camila@fintrack.com", "12345678"));

        assertThat(response.accessToken()).isEqualTo("access_token");
        verify(authenticationManager).authenticate(any());
    }

    @Test
    @DisplayName("Should throw exception when user not found on login")
    void shouldThrowExceptionWhenUserNotFoundOnLogin() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(
                new AuthDTO.LoginRequest("noexiste@fintrack.com", "12345678")))
                .isInstanceOf(AuthException.InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("Should logout and blacklist token")
    void shouldLogoutAndBlacklistToken() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        AuthDTO.MessageResponse response = authService.logout("some_token");

        assertThat(response.message()).isEqualTo("Logged out successfully");
        verify(valueOperations).set(eq("blacklist:some_token"), eq("revoked"), anyLong(), any());
    }
}
