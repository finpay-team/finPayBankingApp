package com.finpay.service.impl;

import com.finpay.dto.UserRegistrationRequest;
import com.finpay.dto.UserResponse;
import com.finpay.entity.Role;
import com.finpay.entity.User;
import com.finpay.exception.BadRequestException;
import com.finpay.repository.RoleRepository;
import com.finpay.repository.UserRepository;
import com.finpay.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse registerUser(UserRegistrationRequest request) {


        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered.");
        }

        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found. Database setup error."));

        User newUser = User.builder()
                .email(request.getEmail())
                // NEVER store plain text passwords. Hash it immediately.
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .active(true)
                .emailVerified(false)
                .locked(false)
                .softDeleted(false)
                .build();

        User savedUser = userRepository.save(newUser);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.isActive()
        );
    }
}