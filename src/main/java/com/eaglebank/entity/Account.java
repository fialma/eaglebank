package com.eaglebank.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Account {
    @Id
    private String accountNumber; // e.g., 01234567
    private String sortCode = "10-10-10"; // Fixed sort code as per spec
    private String name;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private double balance;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdTimestamp;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedTimestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    public enum AccountType {
        personal
    }

    public enum Currency {
        GBP
    }
}