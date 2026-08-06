package com.finpay.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens", schema = "auth")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "rtk_uid")
    private UUID id;

    @Column(name = "rtk_tkn", nullable = false, unique = true, length = 1000)
    private String token;

    @Column(name = "rtk_exp_time", nullable = false)
    private Instant expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usr_uid", nullable = false)
    private User user;

    @Column(name = "rtk_rvk_flg", nullable = false)
    private boolean revoked = false;

    @Column(name = "crte_usr_uid")
    private UUID createdByUserUid;

    @CreationTimestamp
    @Column(name = "crte_time", updatable = false)
    private LocalDateTime createdTime;

    @Column(name = "upd_usr_uid")
    private UUID updatedByUserUid;

    @UpdateTimestamp
    @Column(name = "upd_time")
    private LocalDateTime updatedTime;
}