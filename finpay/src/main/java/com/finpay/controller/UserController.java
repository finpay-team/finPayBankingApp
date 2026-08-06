package com.finpay.controller;

import com.finpay.dto.ApiResponse;
import com.finpay.dto.UserRegistrationRequest;
import com.finpay.dto.UserResponse;
import com.finpay.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User registration and profile APIs")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Create a new user account")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        log.info("Registration request received for email: {}", request.getEmail());
        UserResponse createdUser = userService.registerUser(request);

        ApiResponse<UserResponse> response = new ApiResponse<>(
                true,
                "User registered successfully",
                createdUser
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get the profile of the authenticated user")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        log.info("Fetching profile for: {}", email);

        UserResponse userProfile = userService.getCurrentUser(email);

        ApiResponse<UserResponse> response = new ApiResponse<>(
                true,
                "Profile fetched successfully",
                userProfile
        );
        return ResponseEntity.ok(response);
    }
}