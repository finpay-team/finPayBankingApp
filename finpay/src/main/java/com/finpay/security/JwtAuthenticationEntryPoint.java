package com.finpay.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finpay.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // Inject the Spring-managed ObjectMapper so that the JSR-310 module
    // (auto-registered by Spring Boot) is available to serialize Java 8
    // date/time types such as LocalDateTime.
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.warn("Unauthorized access attempt to {}: {}", request.getRequestURI(), authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<Void> apiResponse = new ApiResponse<>(
                false,
                "Unauthorized: " + authException.getMessage(),
                null
        );

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}