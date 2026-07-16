package com.finpay.service;

import com.finpay.dto.UserRegistrationRequest;
import com.finpay.dto.UserResponse;

public interface UserService {

    UserResponse registerUser(UserRegistrationRequest request);
}
