package com.finpay.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 25, message = "Email must not exceed 25 characters")
    private String email;


    @NotBlank(message = "Password is required")
    @Size(
            min = 12,
            max = 25,
            message = "Password must be between 12 and 25 characters"
    )
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain uppercase, lowercase, number and special character"
    )
    private String password;


    @NotBlank(message = "First name is required")
    @Size(
            min = 2,
            max = 25,
            message = "First name must be between 2 and 25 characters"
    )
    @Pattern(
            regexp = "^[A-Za-z]+(?:[ '-][A-Za-z]+)*$",
            message = "First name contains invalid characters"
    )
    private String firstName;


    @NotBlank(message = "Last name is required")
    @Size(
            min = 2,
            max = 25,
            message = "Last name must be between 2 and 25 characters"
    )
    @Pattern(
            regexp = "^[A-Za-z]+(?:[ '-][A-Za-z]+)*$",
            message = "Last name contains invalid characters"
    )
    private String lastName;
}