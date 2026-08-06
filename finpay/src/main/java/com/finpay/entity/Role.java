package com.finpay.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "roles", schema = "auth")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @CreationTimestamp
    @Column(name = "crte_time", updatable = false)
    private LocalDateTime createdTime;

    @Column(name = "upd_usr_uid")
    private UUID updatedByUserUid;

    @UpdateTimestamp
    @Column(name = "upd_time")
    private LocalDateTime updatedTime;

    @ManyToMany(mappedBy = "roles")
    @Builder.Default
    private Set<User> users = new HashSet<>();
}
