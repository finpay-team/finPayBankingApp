package com.finpay.service.impl;

import com.finpay.dto.LoginRequest;
import com.finpay.dto.LoginResponse;
import com.finpay.dto.LogoutRequest;
import com.finpay.dto.RefreshTokenRequest;
import com.finpay.dto.RefreshTokenResponse;
import com.finpay.entity.RefreshToken;
import com.finpay.entity.User;
import com.finpay.repository.RefreshTokenRepository;
import com.finpay.repository.UserRepository;
import com.finpay.service.AuthService;
import com.finpay.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

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
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getId(), "ROLE_USER");
        String refreshTokenString = jwtTokenProvider.generateRefreshToken(user.getEmail(), user.getId());

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(refreshTokenString)
                .user(user)
                .expiryDate(Instant.now().plusMillis(jwtRefreshExpirationDate))
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenString)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .role("ROLE_USER")
                .build();
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request){

        String token = request.getRefreshToken();
        if(!jwtTokenProvider.validateToken(token)){
            throw new RuntimeException("Invalid or expired refresh token");
        }

        RefreshToken existingRefreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found or revoked"));

        if (existingRefreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(existingRefreshToken);
            throw new RuntimeException("Refresh token has expired");
        }
        
        String tokenType = jwtTokenProvider.getTokenType(token);
        if(!"REFRESH".equals(tokenType)){
            throw new RuntimeException("Invalid token type. Expected Refresh token.");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isActive() || user.isLocked()) {
            throw new RuntimeException("User account is disabled or locked");
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getId(), "ROLE_USER");

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(token)
                .tokenType("Bearer")
                .build();
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request) {
        refreshTokenRepository.deleteByToken(request.getRefreshToken());
    }
}