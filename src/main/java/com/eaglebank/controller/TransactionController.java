package com.eaglebank.controller;

import com.eaglebank.dto.transaction.CreateTransactionRequest;
import com.eaglebank.dto.transaction.ListTransactionsResponse;
import com.eaglebank.dto.transaction.TransactionResponse;
import com.eaglebank.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/accounts/{accountNumber}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@PathVariable String accountNumber, @Valid @RequestBody CreateTransactionRequest request) {
        String authenticatedUserId = getCurrentUserId();
        TransactionResponse transactionResponse = transactionService.createTransaction(accountNumber, authenticatedUserId, request);
        return new ResponseEntity<>(transactionResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ListTransactionsResponse> listAccountTransaction(@PathVariable String accountNumber) {
        String authenticatedUserId = getCurrentUserId();
        ListTransactionsResponse transactions = transactionService.listAccountTransactions(accountNumber, authenticatedUserId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> fetchAccountTransactionByID(@PathVariable String accountNumber, @PathVariable String transactionId) {
        String authenticatedUserId = getCurrentUserId();
        TransactionResponse transactionResponse = transactionService.getAccountTransactionById(accountNumber, transactionId, authenticatedUserId);
        return ResponseEntity.ok(transactionResponse);
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new com.eaglebank.exception.UnauthorizedException();
        }
        return ((UserDetails) authentication.getPrincipal()).getUsername();
    }
}