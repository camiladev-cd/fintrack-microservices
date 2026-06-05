package com.fintrack.auth.service;

import com.fintrack.auth.dto.AuthDTO;
import com.fintrack.auth.exception.AuthException;
import com.fintrack.auth.model.User;
import com.fintrack.auth.repository.UserRepository;
import com.fintrack.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository             userRepository;
    private final PasswordEncoder            passwordEncoder;
    private final JwtService                 jwtService;
    private final AuthenticationManager      authenticationManager;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklist:";

    @Transactional
    public AuthDTO.AuthResponse register(AuthDTO.RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new AuthException.EmailAlreadyExistsException(request.email());
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        userRepository.save(user);
        log.info("New user registered: {}", user.getEmail());

        return buildAuthResponse(user);
    }

    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthException.InvalidCredentialsException());

        log.info("User logged in: {}", user.getEmail());
        return buildAuthResponse(user);
    }

    public AuthDTO.AuthResponse refresh(AuthDTO.RefreshRequest request) {
        String email = jwtService.extractEmail(request.refreshToken());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException.InvalidCredentialsException());

        if (!jwtService.isTokenValid(request.refreshToken(), user)) {
            throw new AuthException.InvalidTokenException();
        }

        return buildAuthResponse(user);
    }

    public AuthDTO.MessageResponse logout(String token) {
        redisTemplate.opsForValue().set(
                BLACKLIST_PREFIX + token,
                "revoked",
                24, TimeUnit.HOURS
        );
        log.info("Token revoked and added to blacklist");
        return new AuthDTO.MessageResponse("Logged out successfully");
    }

    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private AuthDTO.AuthResponse buildAuthResponse(User user) {
        String accessToken  = jwtService.generateAccessToken(user, user.getId());
        String refreshToken = jwtService.generateRefreshToken(user);

        AuthDTO.UserInfo userInfo = new AuthDTO.UserInfo(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().name()
        );

        return AuthDTO.AuthResponse.of(accessToken, refreshToken, jwtService.getExpiration(), userInfo);
    }
}