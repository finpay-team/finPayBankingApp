package com.finpay.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles", schema = "auth")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "rol_uid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "rol_name", unique = true, nullable = false)
    private String name;

    @Column(name = "rol_desc")
    private String description;

    @Column(name = "crte_usr_uid")
    private UUID createdByUserUid;

    @Column(name = "crte_time", updatable = false)
    private LocalDateTime createdTime = LocalDateTime.now();

    @Column(name = "upd_usr_uid")
    private UUID updatedByUserUid;

    @Column(name = "upd_time")
    private LocalDateTime updatedTime = LocalDateTime.now();
}
