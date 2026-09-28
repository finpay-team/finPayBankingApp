package com.finpay.service;


public interface EmailService {

    /**
     * Sends the password reset email to the given address.
     * @param to       recipient email address
     * @param resetUrl  fully-formed resst link (e.g. https://app.finpay.com/resest-password?token=...)
     */
    void sendPasswordResetEmail(String to, String resetUrl);
}
