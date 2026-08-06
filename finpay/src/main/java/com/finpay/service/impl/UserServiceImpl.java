package com.finpay.service.impl;

import com.finpay.dto.UserRegistrationRequest;
import com.finpay.dto.UserResponse;
import com.finpay.entity.Role;
import com.finpay.entity.User;
import com.finpay.exception.BadRequestException;
import com.finpay.exception.ResourceNotFoundException;
import com.finpay.repository.RoleRepository;
import com.finpay.repository.UserRepository;
import com.finpay.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_ROLE = "ROLE_USER";

    @Override
    @Transactional
    public UserResponse registerUser(UserRegistrationRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed - email already exists: {}", request.getEmail());
            throw new BadRequestException("Email is already registered.");
        }

        Role defaultRole = roleRepository.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> {
                    log.error("Default role '{}' not found in database", DEFAULT_ROLE);
                    return new ResourceNotFoundException("Default role not found. Database setup error.");
                });

        Set<Role> roles = new HashSet<>();
        roles.add(defaultRole);

        User newUser = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .active(true)
                .emailVerified(false)
                .locked(false)
                .softDeleted(false)
                .roles(roles)
                .build();

        User savedUser = userRepository.save(newUser);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        return UserResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .isActive(savedUser.isActive())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        log.debug("Fetching current user profile for: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .isActive(user.isActive())
                .build();
    }
}