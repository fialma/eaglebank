package com.eaglebank.controller;

import com.eaglebank.dto.account.BankAccountResponse;
import com.eaglebank.dto.account.CreateBankAccountRequest;
import com.eaglebank.dto.account.ListBankAccountsResponse;
import com.eaglebank.dto.account.UpdateBankAccountRequest;
import com.eaglebank.service.AccountService;
import com.eaglebank.exception.ForbiddenException;
import com.eaglebank.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    SecurityUtil securityUtil;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<BankAccountResponse> createAccount(@Valid @RequestBody CreateBankAccountRequest request) {
        String authenticatedUserId = securityUtil.getCurrentUserId();
        if (!authenticatedUserId.equals(request.getUserId())) {
            throw new ForbiddenException("You can only create accounts for yourself.");
        }
        BankAccountResponse accountResponse = accountService.createAccount(request);
        return new ResponseEntity<>(accountResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ListBankAccountsResponse> listAccounts() {
        String authenticatedUserId = securityUtil.getCurrentUserId();
        ListBankAccountsResponse accounts = accountService.listAccounts(authenticatedUserId);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<BankAccountResponse> fetchAccountByAccountNumber(@PathVariable String accountNumber) {
        String authenticatedUserId = securityUtil.getCurrentUserId();
        BankAccountResponse accountResponse = accountService.getAccountByAccountNumber(accountNumber);
        if (!accountResponse.getUserId().equals(authenticatedUserId)){
            throw new ForbiddenException("You are not authorized to fetch this account detail");
        }
        return ResponseEntity.ok(accountResponse);
    }

    @PatchMapping("/{accountNumber}")
    public ResponseEntity<BankAccountResponse> updateAccountByAccountNumber(@PathVariable String accountNumber, @Valid @RequestBody UpdateBankAccountRequest request) {
        String authenticatedUserId = securityUtil.getCurrentUserId();
        BankAccountResponse accountResponse = accountService.updateAccount(accountNumber, authenticatedUserId, request);
        return ResponseEntity.ok(accountResponse);
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> deleteAccountByAccountNumber(@PathVariable String accountNumber) {
        String authenticatedUserId = securityUtil.getCurrentUserId();
        accountService.deleteAccount(accountNumber, authenticatedUserId);
        return ResponseEntity.noContent().build();
    }


}