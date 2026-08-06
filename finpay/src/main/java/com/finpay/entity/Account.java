package com.finpay.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Account entity mapped to {@code finpay.accounts} (see database/Schemas/auth/accounts.sql).
 *
 * Column names intentionally follow the existing DB schema (snake_case, NOT the
 * {@code *_uid} convention used by the {@code auth} schema) so that
 * {@code spring.jpa.hibernate.ddl-auto=validate} matches the database exactly.
 */
@Entity
@Table(name = "accounts", schema = "finpay")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "account_number", length = 20, unique = true, nullable = false)
    private String accountNumber;

    @Column(name = "account_name", length = 100, nullable = false)
    private String accountName;

    @Column(name = "account_type", length = 30, nullable = false)
    private String accountType;

    @Column(name = "currency", length = 10, nullable = false)
    private String currency;

    @Column(name = "balance", precision = 19, scale = 4, nullable = false)
    private BigDecimal balance;

    @Column(name = "available_balance", precision = 19, scale = 4, nullable = false)
    private BigDecimal availableBalance;

    @Column(name = "account_status", length = 20, nullable = false)
    private String accountStatus;

    @Column(name = "branch_code", length = 20)
    private String branchCode;

    @Column(name = "ifsc_code", length = 20)
    private String ifscCode;

    @Column(name = "daily_transfer_limit", precision = 19, scale = 4)
    private BigDecimal dailyTransferLimit;

    @Column(name = "monthly_transfer_limit", precision = 19, scale = 4)
    private BigDecimal monthlyTransferLimit;

    /** Optimistic locking version column (BIGINT DEFAULT 0). */
    @Version
    @Column(name = "version")
    private Long version;

    /** Owning side of the FK: {@code user_id} -> {@code auth.users(usr_uid)}. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_account_user"))
    private User user;

    @Column(name = "opened_at", nullable = false)
    private OffsetDateTime openedAt;

    @Column(name = "closed_at")
    private OffsetDateTime closedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public boolean isActive() {
        return this.accountStatus != null && "ACTIVE".equalsIgnoreCase(this.accountStatus)
                && this.closedAt == null;
    }
}
