package com.finpay.service;

import com.finpay.dto.ForgotPasswordRequest;
import com.finpay.dto.ResetPasswordRequest;

public interface PasswordResetService {
    // Sends (or pretends to send) the reset link
    void sendPasswordResetToken(ForgotPasswordRequest request);

    // Verifies token + applies the new password
    void resetPassword(ResetPasswordRequest request);
}
