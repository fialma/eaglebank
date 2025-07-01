package com.eaglebank.controller;

import com.eaglebank.dto.transaction.CreateTransactionRequest;
import com.eaglebank.dto.transaction.ListTransactionsResponse;
import com.eaglebank.dto.transaction.TransactionResponse;
import com.eaglebank.service.TransactionService;
import com.eaglebank.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/accounts/{accountNumber}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    SecurityUtil securityUtil;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@PathVariable String accountNumber, @Valid @RequestBody CreateTransactionRequest request) {
        String authenticatedUserId = securityUtil.getCurrentUserId();
        TransactionResponse transactionResponse = transactionService.createTransaction(accountNumber, authenticatedUserId, request);
        return new ResponseEntity<>(transactionResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ListTransactionsResponse> listAccountTransaction(@PathVariable String accountNumber) {
        String authenticatedUserId = securityUtil.getCurrentUserId();
        ListTransactionsResponse transactions = transactionService.listAccountTransactions(accountNumber, authenticatedUserId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> fetchAccountTransactionByID(@PathVariable String accountNumber, @PathVariable String transactionId) {
        String authenticatedUserId = securityUtil.getCurrentUserId();
        TransactionResponse transactionResponse = transactionService.getAccountTransactionById(accountNumber, transactionId, authenticatedUserId);
        return ResponseEntity.ok(transactionResponse);
    }
}