package com.eaglebank.repository;

import com.eaglebank.entity.Transaction;
import com.eaglebank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByAccount(Account account);
    Optional<Transaction> findByIdAndAccount(String transactionId, Account account);
}