package com.eaglebank.service;

import com.eaglebank.dto.transaction.CreateTransactionRequest;
import com.eaglebank.dto.transaction.ListTransactionsResponse;
import com.eaglebank.dto.transaction.TransactionResponse;

public interface TransactionService {
    TransactionResponse createTransaction(String accountNumber, String userId, CreateTransactionRequest request);
    ListTransactionsResponse listAccountTransactions(String accountNumber, String userId);
    TransactionResponse getAccountTransactionById(String accountNumber, String transactionId, String userId);
}
