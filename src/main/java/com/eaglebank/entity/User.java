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
@Table(name = "users") // Renamed to avoid conflict with SQL keyword
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    private String id; // e.g., usr-abc123
    private String name;

    @Embedded
    private Address address;

    private String phoneNumber;
    private String email;
    private String password; // Store hashed passwords

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdTimestamp;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedTimestamp;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Account> accounts;
}