package com.eaglebank.controller;

import com.eaglebank.dto.account.BankAccountResponse;
import com.eaglebank.dto.account.CreateBankAccountRequest;
import com.eaglebank.dto.account.ListBankAccountsResponse;
import com.eaglebank.dto.account.UpdateBankAccountRequest;
import com.eaglebank.service.AccountService;
import com.eaglebank.exception.ForbiddenException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<BankAccountResponse> createAccount(@Valid @RequestBody CreateBankAccountRequest request) {
        String authenticatedUserId = getCurrentUserId();
        if (!authenticatedUserId.equals(request.getUserId())) {
            throw new ForbiddenException("You can only create accounts for yourself.");
        }
        BankAccountResponse accountResponse = accountService.createAccount(request);
        return new ResponseEntity<>(accountResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ListBankAccountsResponse> listAccounts() {
        String authenticatedUserId = getCurrentUserId();
        ListBankAccountsResponse accounts = accountService.listAccounts(authenticatedUserId);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<BankAccountResponse> fetchAccountByAccountNumber(@PathVariable String accountNumber) {
        String authenticatedUserId = getCurrentUserId();
        BankAccountResponse accountResponse = accountService.getAccountByAccountNumber(accountNumber, authenticatedUserId);
        return ResponseEntity.ok(accountResponse);
    }

    @PatchMapping("/{accountNumber}")
    public ResponseEntity<BankAccountResponse> updateAccountByAccountNumber(@PathVariable String accountNumber, @Valid @RequestBody UpdateBankAccountRequest request) {
        String authenticatedUserId = getCurrentUserId();
        BankAccountResponse accountResponse = accountService.updateAccount(accountNumber, authenticatedUserId, request);
        return ResponseEntity.ok(accountResponse);
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> deleteAccountByAccountNumber(@PathVariable String accountNumber) {
        String authenticatedUserId = getCurrentUserId();
        accountService.deleteAccount(accountNumber, authenticatedUserId);
        return ResponseEntity.noContent().build();
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new com.eaglebank.exception.UnauthorizedException();
        }
        return ((UserDetails) authentication.getPrincipal()).getUsername();
    }
}