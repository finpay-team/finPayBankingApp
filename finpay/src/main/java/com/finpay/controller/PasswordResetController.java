package com.finpay.controller;

import com.finpay.dto.ApiResponse;
import com.finpay.dto.ForgotPasswordRequest;
import com.finpay.dto.ResetPasswordRequest;
import com.finpay.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")   // Already permitAll() in SecurityConfig
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    // POST /api/v1/auth/forget-password
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forGotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.sendPasswordResetToken(request);

        // Response is deliberately vague to avoid revealing which emails exist
        ApiResponse<String> response = new ApiResponse<>(
                true,
                "If an account exists for that email, a password reset link has been sent.",
                null
        );
        return ResponseEntity.ok(response);
    }

    // POST /api/v1/auth/reset-password
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);

        ApiResponse<String> response = new ApiResponse<>(
                true,
                "Password has been reset successfully. You can now log in.",
                null
        );
        return ResponseEntity.ok(response);
    }
}
