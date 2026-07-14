package com.finpay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@Table(name = "users", schema = "auth")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "usr_uid", updatable = false, nullable = false)
    UUID id;

    @Column(name = "usr_eml", unique = true, nullable = false)
    private String email;

    @Column(name = "usr_hwd_hsh", nullable = false)
    private String passwordHash;

    @Column(name = "usr_fst_name", nullable = false)
    private String firstName;

    @Column(name = "usr_lst_name", nullable = false)
    private String lastName;

    @Column(name = "usr_actv_flg", nullable = false)
    private boolean active = true;

    @Column(name = "usr_eml_vrf_flg", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "usr_lck_flg", nullable = false)
    private boolean locked = false;

    @Column(name = "sft_del", nullable = false)
    private boolean softDeleted = false;

    @Column(name = "crte_usr_uid")
    private UUID createdByUserUid;

    @Column(name = "crte_time", updatable = false)
    private LocalDateTime updatedByUserUid;

    @Column(name = "upd_time")
    private LocalDateTime updatedTime = LocalDateTime.now();
}
