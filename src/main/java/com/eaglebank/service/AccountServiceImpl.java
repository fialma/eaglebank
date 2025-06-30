package com.eaglebank.service;

import com.eaglebank.dto.account.BankAccountResponse;
import com.eaglebank.dto.account.CreateBankAccountRequest;
import com.eaglebank.dto.account.ListBankAccountsResponse;
import com.eaglebank.dto.account.UpdateBankAccountRequest;
import com.eaglebank.entity.Account;
import com.eaglebank.entity.User;
import com.eaglebank.exception.AccountNotFoundException;
import com.eaglebank.exception.ForbiddenException;
import com.eaglebank.exception.UserNotFoundException;
import com.eaglebank.repository.AccountRepository;
import com.eaglebank.repository.UserRepository;
import com.eaglebank.util.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountServiceImpl(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BankAccountResponse createAccount(CreateBankAccountRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + request.getUserId()));

        Account account = new Account();
        account.setAccountNumber(IdGenerator.generateAccountNumber());
        account.setName(request.getName());
        account.setAccountType(request.getAccountType());
        account.setBalance(new BigDecimal("0.00")); // New accounts start with 0 balance
        account.setCurrency(Account.Currency.GBP); // Default to GBP
        account.setUser(user);
        account.setCreatedTimestamp(LocalDateTime.now());
        account.setUpdatedTimestamp(LocalDateTime.now());
        Account savedAccount = accountRepository.save(account);
        return mapToBankAccountResponse(savedAccount);
    }

    public ListBankAccountsResponse listAccounts(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        List<BankAccountResponse> accounts = accountRepository.findByUser(user)
                .stream()
                .map(this::mapToBankAccountResponse)
                .collect(Collectors.toList());
        ListBankAccountsResponse response = new ListBankAccountsResponse();
        response.setAccounts(accounts);
        return response;
    }


    public BankAccountResponse getAccountByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        return mapToBankAccountResponse(account);
    }

    @Transactional
    public BankAccountResponse updateAccount(String accountNumber, String userId, UpdateBankAccountRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        if(!account.getUser().getId().equals(userId)){
            throw new ForbiddenException("You can only modify your accounts.");
        }

        Optional.ofNullable(request.getName()).ifPresent(account::setName);
        Optional.ofNullable(request.getAccountType()).ifPresent(account::setAccountType);
        account.setUpdatedTimestamp(LocalDateTime.now());

        Account updatedAccount = accountRepository.save(account);
        return mapToBankAccountResponse(updatedAccount);
    }

    @Transactional
    public void deleteAccount(String accountNumber, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        if(!account.getUser().getId().equals(userId)){
            throw new ForbiddenException("You can only delete your accounts.");
        }

        accountRepository.delete(account);
    }

    private BankAccountResponse mapToBankAccountResponse(Account account) {
        BankAccountResponse response = new BankAccountResponse();
        response.setAccountNumber(account.getAccountNumber());
        response.setSortCode(account.getSortCode());
        response.setName(account.getName());
        response.setAccountType(account.getAccountType());
        response.setBalance(account.getBalance());
        response.setCurrency(account.getCurrency());
        response.setUserId(account.getUser().getId());
        response.setCreatedTimestamp(account.getCreatedTimestamp());
        response.setUpdatedTimestamp(account.getUpdatedTimestamp());
        return response;
    }
}