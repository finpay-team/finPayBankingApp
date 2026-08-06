package com.finpay.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "auth")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "usr_uid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "usr_eml", unique = true, nullable = false)
    private String email;

    @Column(name = "usr_pwd_hsh", nullable = false)
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

    @CreationTimestamp
    @Column(name = "crte_time", updatable = false)
    private LocalDateTime createdTime;

    @Column(name = "upd_usr_uid")
    private UUID updatedByUserUid;

    @UpdateTimestamp
    @Column(name = "upd_time")
    private LocalDateTime updatedTime;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            schema = "auth",
            joinColumns = @JoinColumn(name = "usr_uid"),
            inverseJoinColumns = @JoinColumn(name = "rol_uid")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        this.roles.add(role);
        role.getUsers().add(this);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
        role.getUsers().remove(this);
    }
}