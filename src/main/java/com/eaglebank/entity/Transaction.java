package com.eaglebank.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
    @Id
    private String id; // e.g., tan-123abc
    private double amount;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private String reference;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdTimestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_number")
    private Account account;

    public enum Currency {
        GBP
    }

    public enum TransactionType {
        deposit, withdrawal
    }
}