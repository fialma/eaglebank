package com.eaglebank.dto.account;

import com.eaglebank.entity.Account;
import lombok.Data;

@Data
public class UpdateBankAccountRequest {
    private String name;

    private Account.AccountType accountType;
}