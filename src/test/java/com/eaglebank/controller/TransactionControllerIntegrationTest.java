package com.eaglebank.controller;

import com.eaglebank.TestcontainersInitializer;
import com.eaglebank.dto.auth.AuthRequest;
import com.eaglebank.dto.auth.AuthResponse;
import com.eaglebank.dto.error.BadRequestErrorResponse;
import com.eaglebank.dto.transaction.CreateTransactionRequest;
import com.eaglebank.dto.transaction.ListTransactionsResponse;
import com.eaglebank.dto.transaction.TransactionResponse;
import com.eaglebank.entity.Account;
import com.eaglebank.entity.Address;
import com.eaglebank.entity.Transaction;
import com.eaglebank.entity.User;
import com.eaglebank.repository.AccountRepository;
import com.eaglebank.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test") // Use a dedicated test profile for H2
@Transactional // Rollback transactions after each test
@ContextConfiguration(initializers = TestcontainersInitializer.class)
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;



    private User user1;
    private String user1Id;
    private String account1Number;
    private String user2Id;
    private String account2Number;
    private String user1Token;

    @BeforeEach
    void setUp() throws Exception {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll(); // Clean up before each test

        // Create user 1
        User user1 = new User();
        user1.setId("usr-test999");
        user1.setName("Integration Test User 1");
        user1.setEmail("integration1@example.com");
        user1.setPhoneNumber("+447111222333");
        user1.setAddress(new Address("1 Integration St", null, null, "Test City", "Test County", "TS1 1TS"));
        user1.setPassword(passwordEncoder.encode("password"));
        user1.setCreatedTimestamp(java.time.LocalDateTime.now());
        user1.setUpdatedTimestamp(java.time.LocalDateTime.now());
        userRepository.save(user1);
        this.user1Id = user1.getId();
        this.user1 = user1;

        Account account1 = new Account();
        account1.setUser(user1);
        account1.setAccountNumber("012345Test");
        account1.setName("TestAccountName1");
        account1.setBalance(BigDecimal.valueOf(100.00));

        accountRepository.save(account1);
        this.account1Number = account1.getAccountNumber();

        // Create user 2
        User user2 = new User();
        user2.setId("usr-int-test2");
        user2.setName("Integration Test User 2");
        user2.setEmail("integration2@example.com");
        user2.setPhoneNumber("+447444555666");
        user2.setAddress(new Address("2 Integration Ave", null, null, "Other City", "Other County", "OT2 2OT"));
        user2.setPassword(passwordEncoder.encode("password"));
        user2.setCreatedTimestamp(java.time.LocalDateTime.now());
        user2.setUpdatedTimestamp(java.time.LocalDateTime.now());
        userRepository.save(user2);
        this.user2Id = user2.getId();

        Account account2 = new Account();
        account2.setUser(user2);
        account2.setAccountNumber("012222Test");
        account2.setName("TestAccountName2");

        accountRepository.save(account2);
        this.account2Number = account2.getAccountNumber();

        // Authenticate user 1 to get a token
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("integration1@example.com");
        authRequest.setPassword("password");

        MvcResult authResult = mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String authResponseJson = authResult.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(authResponseJson, AuthResponse.class);
        user1Token = authResponse.getToken();
    }

    @Test
    void depositTransaction_Success() throws Exception {
        var transactionRequest = new CreateTransactionRequest();
        transactionRequest.setType(Transaction.TransactionType.DEPOSIT);
        transactionRequest.setAmount(BigDecimal.valueOf(50));
        transactionRequest.setReference("Test Deposit");
        transactionRequest.setCurrency(Transaction.Currency.GBP);

        mockMvc.perform(post("/v1/accounts/{accountNumber}/transactions", account1Number)
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isCreated());

        Optional<User> user = userRepository.findById(user1Id);
        // Verify account is in database
        var account = accountRepository.findByAccountNumberAndUser(account1Number, user.get()).get();
        assertEquals(account.getBalance().doubleValue(), 150.00D);
    }


    @Test
    void withdrawTransaction_Success() throws Exception {
        var transactionRequest = new CreateTransactionRequest();
        transactionRequest.setType(Transaction.TransactionType.WITHDRAWAL);
        transactionRequest.setAmount(BigDecimal.valueOf(48));
        transactionRequest.setReference("Test Withdraw");
        transactionRequest.setCurrency(Transaction.Currency.GBP);

        mockMvc.perform(post("/v1/accounts/{accountNumber}/transactions", account1Number)
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isCreated());

        Optional<User> user = userRepository.findById(user1Id);
        // Verify account is in database
        var account = accountRepository.findByAccountNumberAndUser(account1Number, user.get()).get();
        assertEquals(account.getBalance().doubleValue(), 52.00D);
    }

    @Test
    void withdrawTransaction_FailForInsufficientFunds() throws Exception {
        var transactionRequest = new CreateTransactionRequest();
        transactionRequest.setType(Transaction.TransactionType.WITHDRAWAL);
        transactionRequest.setAmount(BigDecimal.valueOf(101));
        transactionRequest.setReference("Test Withdraw");
        transactionRequest.setCurrency(Transaction.Currency.GBP);

        mockMvc.perform(post("/v1/accounts/{accountNumber}/transactions", account1Number)
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Insufficient funds for withdrawal. Current balance: 100.0"));
    }

    @Test
    void withdrawTransaction_FailForNotOwnerOfAccount() throws Exception {
        var transactionRequest = new CreateTransactionRequest();
        transactionRequest.setType(Transaction.TransactionType.WITHDRAWAL);
        transactionRequest.setAmount(BigDecimal.valueOf(101));
        transactionRequest.setReference("Test Withdraw");
        transactionRequest.setCurrency(Transaction.Currency.GBP);

        mockMvc.perform(post("/v1/accounts/{accountNumber}/transactions", account2Number)
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You are not allowed to perform operations on other users' accounts."));
    }

    @Test
    void withdrawTransaction_FailForNotExistingAccount() throws Exception {
        var transactionRequest = new CreateTransactionRequest();
        transactionRequest.setType(Transaction.TransactionType.WITHDRAWAL);
        transactionRequest.setAmount(BigDecimal.valueOf(101));
        transactionRequest.setReference("Test Withdraw");
        transactionRequest.setCurrency(Transaction.Currency.GBP);

        mockMvc.perform(post("/v1/accounts/{accountNumber}/transactions", "01notExisting")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Bank account not found with account number: 01notExisting"));
    }

    @Test
    void withdrawTransaction_MissingRequiredData() throws Exception {
        var transactionRequest = new CreateTransactionRequest();
        transactionRequest.setType(Transaction.TransactionType.WITHDRAWAL);
        //missing amount
        transactionRequest.setReference("Test Withdraw");
        transactionRequest.setCurrency(Transaction.Currency.GBP);

        MvcResult result = mockMvc.perform(post("/v1/accounts/{accountNumber}/transactions", "01notExisting")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation Error."))
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        BadRequestErrorResponse response = objectMapper.readValue(responseJson, BadRequestErrorResponse.class);
        Assertions.assertEquals(1, response.getDetails().size());
    }


    @Test
    void fetchTransactionsByAccountId_Success() throws Exception {

        var transactionRequest = new CreateTransactionRequest();
        transactionRequest.setType(Transaction.TransactionType.DEPOSIT);
        transactionRequest.setAmount(BigDecimal.valueOf(50));
        transactionRequest.setReference("Test Deposit");
        transactionRequest.setCurrency(Transaction.Currency.GBP);

        mockMvc.perform(post("/v1/accounts/{accountNumber}/transactions", account1Number)
                .header("Authorization", "Bearer " + user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionRequest)));


        MvcResult result = mockMvc.perform(get("/v1/accounts/{accountId}/transactions", account1Number)
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        ListTransactionsResponse response = objectMapper.readValue(responseJson, ListTransactionsResponse.class);
        Assertions.assertEquals(1,response.getTransactions().size());
    }

    @Test
    void fetchTransactionsByAccountId_FailForNotOwnerOfAccount() throws Exception {

        mockMvc.perform(get("/v1/accounts/{accountId}/transactions", account2Number)
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You are not allowed to access this account's details."));
    }

    @Test
    void fetchTransactionsByAccountId_FailForNotExistingAccount() throws Exception {
        mockMvc.perform(get("/v1/accounts/{accountId}/transactions", "01notExisting")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Bank account not found with account number: 01notExisting"));
    }

    @Test
    void fetchTransactionById_Success() throws Exception {
        // Arrange: create a transaction for user1's account
        var transactionRequest = new CreateTransactionRequest();
        transactionRequest.setType(Transaction.TransactionType.DEPOSIT);
        transactionRequest.setAmount(BigDecimal.valueOf(25));
        transactionRequest.setReference("FetchByIdTest");
        transactionRequest.setCurrency(Transaction.Currency.GBP);

        // Create the transaction
        MvcResult createResult = mockMvc.perform(post("/v1/accounts/{accountNumber}/transactions", account1Number)
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract transactionId from response
        String createResponseJson = createResult.getResponse().getContentAsString();
        TransactionResponse createdTransaction = objectMapper.readValue(createResponseJson, TransactionResponse.class);
        String transactionId = createdTransaction.getId();

        // Act & Assert: fetch the transaction by ID
        mockMvc.perform(get("/v1/accounts/{accountNumber}/transactions/{transactionId}", account1Number, transactionId)
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId))
                .andExpect(jsonPath("$.amount").value(25))
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.reference").value("FetchByIdTest"))
                .andExpect(jsonPath("$.accountNumber").value(account1Number));
    }

    @Test
    void fetchTransactionsByTransaction_FailForNotOwnerOfAccount() throws Exception {
        mockMvc.perform(get("/v1/accounts/{accountNumber}/transactions/{transactionId}", account2Number, "transactionId")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You are not allowed to access this account's details."));
    }

    @Test
    void fetchTransactionsByTransactionId_FailForNotExistingAccount() throws Exception {
        mockMvc.perform(get("/v1/accounts/{accountId}/transactions/{transactionId}", "account1Number", "transactionId")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Bank account not found with account number: account1Number"));
    }

    @Test
    void fetchTransactionsByTransactionId_FailForNotExistingTransactionId() throws Exception {
        mockMvc.perform(get("/v1/accounts/{accountId}/transactions/{transactionId}", account1Number, "transactionId")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Transaction not found with ID: transactionId for account: 012345Test"));
    }

    @Test
    void fetchTransactionsByTransactionId_FailForNotExistingTransactionIdInTheRequestedAccount() throws Exception {

        Account account2User1 = new Account();
        account2User1.setUser(user1);
        account2User1.setAccountNumber("010002Test");
        account2User1.setName("TestAccountName2");
        account2User1.setBalance(BigDecimal.valueOf(100.00));

        accountRepository.save(account2User1);


        var transactionRequest = new CreateTransactionRequest();
        transactionRequest.setType(Transaction.TransactionType.DEPOSIT);
        transactionRequest.setAmount(BigDecimal.valueOf(25));
        transactionRequest.setReference("FetchByIdTest");
        transactionRequest.setCurrency(Transaction.Currency.GBP);

        // Create the transaction
        MvcResult createResult = mockMvc.perform(post("/v1/accounts/{accountNumber}/transactions", account2User1.getAccountNumber())
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract transactionId from response
        String createResponseJson = createResult.getResponse().getContentAsString();
        TransactionResponse createdTransaction = objectMapper.readValue(createResponseJson, TransactionResponse.class);
        String transactionId = createdTransaction.getId();

        mockMvc.perform(get("/v1/accounts/{accountId}/transactions/{transactionId}", account1Number, transactionId)
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }






}