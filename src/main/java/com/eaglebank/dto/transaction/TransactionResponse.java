package com.eaglebank.dto.transaction;

import com.eaglebank.entity.Transaction;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private String id;
    private BigDecimal amount;
    private Transaction.Currency currency;
    private Transaction.TransactionType type;
    private String reference;
    private String accountNumber;
    private String userId;
    private LocalDateTime createdTimestamp;
}