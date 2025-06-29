package com.eaglebank.dto.account;

import lombok.Data;

import java.util.List;

@Data
public class ListBankAccountsResponse {
    List<BankAccountResponse> accounts;
}
