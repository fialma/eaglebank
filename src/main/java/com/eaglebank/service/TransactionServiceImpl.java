package com.eaglebank.service;

import com.eaglebank.dto.transaction.CreateTransactionRequest;
import com.eaglebank.dto.transaction.ListTransactionsResponse;
import com.eaglebank.dto.transaction.TransactionResponse;
import com.eaglebank.entity.Account;
import com.eaglebank.entity.Transaction;
import com.eaglebank.entity.User;
import com.eaglebank.exception.AccountNotFoundException;
import com.eaglebank.exception.InsufficientFundsException;
import com.eaglebank.exception.TransactionNotFoundException;
import com.eaglebank.exception.UserNotFoundException;
import com.eaglebank.repository.AccountRepository;
import com.eaglebank.repository.TransactionRepository;
import com.eaglebank.repository.UserRepository;
import com.eaglebank.util.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TransactionResponse createTransaction(String accountNumber, String userId, CreateTransactionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Account account = accountRepository.findByAccountNumberAndUser(accountNumber, user)
                .orElseThrow(() -> new AccountNotFoundException("Bank account not found with account number: " + accountNumber + " for user: " + userId));

        double newBalance = account.getBalance();
        if (request.getType() == Transaction.TransactionType.deposit) {
            newBalance += request.getAmount();
        } else { // withdrawal
            if (account.getBalance() < request.getAmount()) {
                throw new InsufficientFundsException("Insufficient funds for withdrawal. Current balance: " + account.getBalance());
            }
            newBalance -= request.getAmount();
        }

        account.setBalance(newBalance);
        account.setUpdatedTimestamp(LocalDateTime.now());
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setId(IdGenerator.generateTransactionId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setType(request.getType());
        transaction.setReference(request.getReference());
        transaction.setAccount(account);
        transaction.setCreatedTimestamp(LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToTransactionResponse(savedTransaction);
    }

    public ListTransactionsResponse listAccountTransactions(String accountNumber, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Account account = accountRepository.findByAccountNumberAndUser(accountNumber, user)
                .orElseThrow(() -> new AccountNotFoundException("Bank account not found with account number: " + accountNumber + " for user: " + userId));

        List<TransactionResponse> transactions = transactionRepository.findByAccount(account)
                .stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
        ListTransactionsResponse response = new ListTransactionsResponse();
        response.setTransactions(transactions);
        return response;
    }

    public TransactionResponse getAccountTransactionById(String accountNumber, String transactionId, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Account account = accountRepository.findByAccountNumberAndUser(accountNumber, user)
                .orElseThrow(() -> new AccountNotFoundException("Bank account not found with account number: " + accountNumber + " for user: " + userId));

        Transaction transaction = transactionRepository.findByIdAndAccount(transactionId, account)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with ID: " + transactionId + " for account: " + accountNumber));
        return mapToTransactionResponse(transaction);
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setAmount(transaction.getAmount());
        response.setCurrency(transaction.getCurrency());
        response.setType(transaction.getType());
        response.setReference(transaction.getReference());
        response.setAccountNumber(transaction.getAccount().getAccountNumber());
        response.setUserId(transaction.getAccount().getUser().getId());
        response.setCreatedTimestamp(transaction.getCreatedTimestamp());
        return response;
    }
}