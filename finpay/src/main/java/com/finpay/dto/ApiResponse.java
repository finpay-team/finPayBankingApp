package com.finpay.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    // A handy constructor for when you just want to send a success message without data
    public ApiResponse(Boolean success, String message){
        this.success = success;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    // A handy constructor for when you have data to send
    public ApiResponse(Boolean success, String message, T data){
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
}
