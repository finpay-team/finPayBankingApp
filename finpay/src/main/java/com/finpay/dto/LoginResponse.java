package com.finpay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginResponse {
    private String accessToken;
    private String refereshToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private UUID userId;
    private String email;
    private String role;
}
