package com.finpay.service.impl;

import com.finpay.dto.ForgotPasswordRequest;
import com.finpay.dto.ResetPasswordRequest;
import com.finpay.entity.PasswordResetToken;
import com.finpay.entity.User;
import com.finpay.exception.BadRequestException;
import com.finpay.repository.PasswordResetTokenRepository;
import com.finpay.repository.UserRepository;
import com.finpay.service.EmailService;
import com.finpay.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    // Cryptographically strong randomness for tokens
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.password-reset.token-expiration-minutes}")
    private long tokenExpirationMinutes;

    @Value("${app.frontend.reset-password-url}")
    private String resetPasswordBaseUrl;

    @Override
    @Transactional
    public void sendPasswordResetToken(ForgotPasswordRequest request) {
        // Always silently return the same message whether or not the user exists
        // (prevents email enumeration attacks).
        userRepository.findByEmail(request.getEmail().toLowerCase().trim()).ifPresent(user -> {
            // Don't create tokens for disabled/locked accounts
            if (!user.isActive() || user.isLocked()) return;

            // Invalidation: any previous reset tokens for this user become invalid
            passwordResetTokenRepository.deleteByUser(user);

            String token = generateToken();

            PasswordResetToken entity = PasswordResetToken.builder()
                    .token(token)
                    .user(user)
                    .expiryDate(Instant.now().plusSeconds(tokenExpirationMinutes * 60))
                    .used(false)
                    .build();
            passwordResetTokenRepository.save(entity);

            // Build the clickable reset link and email it via Gmail SMTP
            String resetUrl = resetPasswordBaseUrl + "?token=" + token;
            emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);
        });
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid or expired password reset token"));

        if (resetToken.isUsed()) {
            throw new BadRequestException("This reset token has already been used");
        }
        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new BadRequestException("Password reset token has expired");
        }

        User user = resetToken.getUser();
        if (!user.isActive() || user.isLocked()) {
            throw new BadRequestException("User account is disabled or locked");
        }

        // ✅ Encode with BCrypt using the same PasswordEncoder as registration
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Single-use: mark token as spent
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    // 32 random bytes -> Base64 URL (43 chars), unpredictable and free of padding issues
    private String generateToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
