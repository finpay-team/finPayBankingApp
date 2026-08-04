package com.finpay.controller;

import com.finpay.dto.ApiResponse;
import com.finpay.dto.UserRegistrationRequest;
import com.finpay.dto.UserResponse;
import com.finpay.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUesr(@RequestBody UserRegistrationRequest request){

        UserResponse createdUser = userService.registerUser(request);

        ApiResponse<UserResponse> response = new ApiResponse<>(
                true,
                "User registered successfully",
                createdUser
        );
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication authentication){
        String email = authentication.getName();

        UserResponse userProfile = userService.getCurrentUser(email);

        ApiResponse<UserResponse> response = new ApiResponse<>(
                true,
                "Profile fetched successfully",
                userProfile
                );
        return ResponseEntity.ok(response);
    }

}
