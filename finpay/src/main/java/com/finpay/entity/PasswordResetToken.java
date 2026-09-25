package com.finpay.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens", schema = "auth")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "prt_uuid", updatable = false, nullable = false)
    private UUID id;

    // Raw reset token (matching refresh_tokens style).
    // Security note: for extra safety you could store a SHA-256 hash instead.
    @Column(name = "prt_tkn", nullable = false, unique = true, length = 255)
    private String token;

    @Column(name = "prt_exp_time", nullable = false)
    private Instant expiryDate;

    // Single-use flag - after a successful reset, set to true.
    @Column(name = "prt_used_flg", nullable = false)
    private boolean used = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usr_uid", nullable = false)
    private User user;
}
