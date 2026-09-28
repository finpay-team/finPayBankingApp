package com.finpay.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finpay.dto.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /*@Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException{
        // This triggers when an unauthenticated user requests a secured HTTP resource
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: "+ authException.getMessage());
    }*/
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // Consistent 401 JSON in the same ApiResponse format as the rest of the app
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<String> body = new ApiResponse<>(
                false,
                "Unauthorized: missing or invalid access token",
                null
        );
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
