package com.finpay.service.impl;

import com.finpay.dto.LoginRequest;
import com.finpay.dto.LoginResponse;
import com.finpay.dto.LogoutRequest;
import com.finpay.dto.RefreshTokenRequest;
import com.finpay.dto.RefreshTokenResponse;
import com.finpay.entity.RefreshToken;
import com.finpay.entity.User;
import com.finpay.exception.ResourceNotFoundException;
import com.finpay.exception.UnauthorizedException;
import com.finpay.repository.RefreshTokenRepository;
import com.finpay.repository.UserRepository;
import com.finpay.service.AuthService;
import com.finpay.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-expiration-milliseconds}")
    private long jwtRefreshExpirationDate;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    log.error("User not found after successful authentication: {}", loginRequest.getEmail());
                    return new ResourceNotFoundException("User not found");
                });

        String primaryRole = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.joining(","));

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getId(), primaryRole);
        String refreshTokenString = jwtTokenProvider.generateRefreshToken(user.getEmail(), user.getId());

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(refreshTokenString)
                .user(user)
                .expiryDate(Instant.now().plusMillis(jwtRefreshExpirationDate))
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        log.info("Login successful for user: {}", user.getId());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenString)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .role(primaryRole)
                .build();
    }

    @Override
    @Transactional
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        log.info("Token refresh request received");

        String token = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(token)) {
            log.warn("Invalid or expired refresh token");
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        String tokenType = jwtTokenProvider.getTokenType(token);
        if (!"REFRESH".equals(tokenType)) {
            log.warn("Invalid token type. Expected REFRESH, got: {}", tokenType);
            throw new UnauthorizedException("Invalid token type. Expected Refresh token.");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found for refresh token: {}", email);
                    return new ResourceNotFoundException("User not found");
                });

        if (!user.isActive() || user.isLocked()) {
            log.warn("User account is disabled or locked: {}", email);
            throw new UnauthorizedException("User account is disabled or locked");
        }

        RefreshToken storedToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Refresh token not found in database");
                    return new UnauthorizedException("Refresh token not found. Please login again.");
                });

        if (storedToken.getExpiryDate().isBefore(Instant.now())) {
            log.warn("Refresh token expired in database");
            refreshTokenRepository.delete(storedToken);
            throw new UnauthorizedException("Refresh token has expired. Please login again.");
        }

        String primaryRole = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.joining(","));

        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getId(), primaryRole);

        log.info("Token refreshed successfully for user: {}", user.getId());

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(token)
                .tokenType("Bearer")
                .build();
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request) {
        log.info("Logout request received");
        refreshTokenRepository.deleteByToken(request.getRefreshToken());
        log.info("User logged out successfully");
    }
}