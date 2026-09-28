package com.finpay.service.impl;

import com.finpay.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String fromAddress;

    @Override
    public void sendPasswordResetEmail(String to, String resetUrl) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject("FinPay - Reset your password");

            // Plain-text body. Keep it friendly and include instructions.
            message.setText("Hello,\n\n"
                    + "We received a request to reset the password for your FinPay account.\n\n"
                    + "Click the link below to choose a new password:\n"
                    + resetUrl + "\n\n"
                    + "This link is valid for 30 minutes.\n\n"
                    + "If you did not request this, you can safely ignore this email "
                    + "and your password will not be changed.\n\n"
                    + "Thanks,\nThe FinPay Team");

            // ✅ Send via Gmail SMTP (free up to ~500 emails/day)
            mailSender.send(message);

            log.info("Password reset email sent to {}", to);
        } catch (Exception e) {
            // Mail failure must NOT break the forgot-password response.
            // The token is already stored in DB, so a retry can still use it.
            log.error("Failed to send password reset email to {}: {}", to, e.getMessage(), e);
        }
    }
}
