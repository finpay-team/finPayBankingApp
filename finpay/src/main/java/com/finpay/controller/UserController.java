package com.finpay.controller;

import com.finpay.dto.ApiResponse;
import com.finpay.dto.UserRegistrationRequest;
import com.finpay.dto.UserResponse;
import com.finpay.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
