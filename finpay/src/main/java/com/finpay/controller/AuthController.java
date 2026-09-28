package com.finpay.controller;

import com.finpay.dto.*;
import com.finpay.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);

        ApiResponse<LoginResponse> response = new ApiResponse<>(
                true,
                "Login successful",
                loginResponse
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request){
        RefreshTokenResponse tokenResponse = authService.refreshToken(request);

        ApiResponse<RefreshTokenResponse> response = new ApiResponse<>(
                true,
                "Token refreshed successfully",
                tokenResponse
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@Valid @RequestBody LogoutRequest request){
        authService.logout(request);

        ApiResponse<String> response = new ApiResponse<>(
                true,
                "Logged out successfully",
                null
        );

        return ResponseEntity.ok(response);
    }
}
