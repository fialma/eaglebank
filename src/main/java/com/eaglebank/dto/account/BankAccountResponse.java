package com.eaglebank.dto.account;

import com.eaglebank.entity.Account;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BankAccountResponse {
    private String accountNumber;
    private String sortCode;
    private String name;
    private Account.AccountType accountType;
    private BigDecimal balance;
    private Account.Currency currency;
    private String userId;
    private LocalDateTime createdTimestamp;
    private LocalDateTime updatedTimestamp;
}