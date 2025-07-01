package com.eaglebank.controller;

import com.eaglebank.TestcontainersInitializer;
import com.eaglebank.dto.account.BankAccountResponse;
import com.eaglebank.dto.account.CreateBankAccountRequest;
import com.eaglebank.dto.account.ListBankAccountsResponse;
import com.eaglebank.dto.account.UpdateBankAccountRequest;
import com.eaglebank.dto.auth.AuthRequest;
import com.eaglebank.dto.auth.AuthResponse;
import com.eaglebank.dto.error.BadRequestErrorResponse;
import com.eaglebank.entity.Account;
import com.eaglebank.entity.Address;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test") // Use a dedicated test profile for H2
@Transactional // Rollback transactions after each test
@ContextConfiguration(initializers = TestcontainersInitializer.class)
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;



    private String user1Id;
    private String user2Id;
    private String user1Token;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll(); // Clean up before each test
        accountRepository.deleteAll();

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

        Account account1 = new Account();
        account1.setUser(user1);
        account1.setAccountNumber("012345Test");
        account1.setName("TestAccountName1");

        accountRepository.save(account1);

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
    void createAccount_Success() throws Exception {
        var accountRequest = new CreateBankAccountRequest();
        accountRequest.setName("New Test Account");
        accountRequest.setAccountType(Account.AccountType.PERSONAL);
        accountRequest.setUserId(user1Id);
        MvcResult result =mockMvc.perform(post("/v1/accounts")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Test Account"))
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();

        BankAccountResponse accountResponse = objectMapper.readValue(responseJson, BankAccountResponse.class);

        Optional<User> user = userRepository.findById(user1Id);
        // Verify account is in database
        assertTrue(accountRepository.findByAccountNumberAndUser(accountResponse.getAccountNumber(), user.get()).isPresent());
    }

    @Test
    void createAccount_MissingRequiredData() throws Exception {
        var accountRequest = new CreateBankAccountRequest();
        accountRequest.setAccountType(Account.AccountType.PERSONAL);
        accountRequest.setUserId(user1Id);
        // Missing name

        MvcResult result = mockMvc.perform(post("/v1/accounts")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation Error."))
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        BadRequestErrorResponse response = objectMapper.readValue(responseJson, BadRequestErrorResponse.class);
        Assertions.assertEquals(1, response.getDetails().size());
    }


    @Test
    void fetchAccountsForAuthenticated_Success() throws Exception {
        MvcResult result = mockMvc.perform(get("/v1/accounts")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        ListBankAccountsResponse response = objectMapper.readValue(responseJson, ListBankAccountsResponse.class);

        Assertions.assertEquals(1, response.getAccounts().size());
        Assertions.assertEquals("012345Test", response.getAccounts().get(0).getAccountNumber());
    }

    @Test
    void fetchAccountsByID_Success() throws Exception {
        mockMvc.perform(get("/v1/accounts/{accountNumber}", "012345Test")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("012345Test"))
                .andExpect(jsonPath("$.name").value("TestAccountName1"));
    }

    @Test
    void fetchAccountsByID_NotAuthorized() throws Exception {
        mockMvc.perform(get("/v1/accounts/{accountNumber}", "012222Test")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void fetchAccountsByID_NotFound() throws Exception {
        mockMvc.perform(get("/v1/accounts/{accountNumber}", "012999Test")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateAccountByID_Success() throws Exception {
        UpdateBankAccountRequest request = new UpdateBankAccountRequest();
        request.setName("Updated Test Name");

        mockMvc.perform(patch("/v1/accounts/{accountNumber}", "012345Test")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountNumber").value("012345Test"))
        .andExpect(jsonPath("$.name").value("Updated Test Name"));

    }

    @Test
    void updateAccountByID_NotAuthorized() throws Exception {
        UpdateBankAccountRequest request = new UpdateBankAccountRequest();
        request.setName("Updated Test Name");

        mockMvc.perform(patch("/v1/accounts/{accountNumber}", "012222Test")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

    }

    @Test
    void updateAccountByID_NotFound() throws Exception {
        UpdateBankAccountRequest request = new UpdateBankAccountRequest();
        request.setName("Updated Test Name");

        mockMvc.perform(patch("/v1/accounts/{accountNumber}", "012999Test")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

    }

    @Test
    void deleteAccountByID_Success() throws Exception {
        mockMvc.perform(delete("/v1/accounts/{accountNumber}", "012345Test")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }

    @Test
    void deleteAccountByID_NotAuthorized() throws Exception {
        mockMvc.perform(delete("/v1/accounts/{accountNumber}", "012222Test")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteAccountByID_NotFound() throws Exception {
        mockMvc.perform(delete("/v1/accounts/{accountNumber}", "012999Test")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}