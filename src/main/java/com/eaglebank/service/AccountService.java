package com.eaglebank.service;

import com.eaglebank.dto.account.BankAccountResponse;
import com.eaglebank.dto.account.CreateBankAccountRequest;
import com.eaglebank.dto.account.ListBankAccountsResponse;
import com.eaglebank.dto.account.UpdateBankAccountRequest;

public interface AccountService {

    BankAccountResponse createAccount(CreateBankAccountRequest request);

    ListBankAccountsResponse listAccounts(String userId);

    BankAccountResponse getAccountByAccountNumber(String accountNumber, String userId);

    BankAccountResponse updateAccount(String accountNumber, String userId, UpdateBankAccountRequest request);

    void deleteAccount(String accountNumber, String userId);



}
