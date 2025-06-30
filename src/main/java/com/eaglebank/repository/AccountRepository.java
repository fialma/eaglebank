package com.eaglebank.repository;

import com.eaglebank.entity.Account;
import com.eaglebank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    List<Account> findByUser(User user);
    Optional<Account> findByAccountNumberAndUser(String accountNumber, User user);
    Optional<Account> findByAccountNumber(String accountNumber);
    long countByUser(User user);
}