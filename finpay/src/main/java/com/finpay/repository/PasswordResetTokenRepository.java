package com.finpay.repository;

import com.finpay.entity.PasswordResetToken;
import com.finpay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken> findByToken(String token);

    // Invalidate all previous reset tokens when a new one is requested.
    void deleteByUser(User user);
}
