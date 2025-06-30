package com.eaglebank.controller;

import com.eaglebank.dto.auth.AuthRequest;
import com.eaglebank.dto.auth.AuthResponse;
import com.eaglebank.entity.Address;
import com.eaglebank.entity.User;
import com.eaglebank.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Use a dedicated test profile for H2
@Transactional // Rollback transactions after each test
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user1;

    @BeforeEach
    void setUp() throws Exception {
        // Create user 1
        User user1 = new User();
        user1.setId("usr-int-test1");
        user1.setName("Integration Test User 1");
        user1.setEmail("integration1@example.com");
        user1.setPhoneNumber("+447111222333");
        user1.setAddress(new Address("1 Integration St", null, null, "Test City", "Test County", "TS1 1TS"));
        user1.setPassword(passwordEncoder.encode("password"));
        user1.setCreatedTimestamp(java.time.LocalDateTime.now());
        user1.setUpdatedTimestamp(java.time.LocalDateTime.now());
        userRepository.save(user1);
        this.user1 = user1;
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll(); // Clean up after each test
    }

    @Test
    void authorizeUser_Success() throws Exception {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail(user1.getEmail());
        authRequest.setPassword("password");

        MvcResult authResult = mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String authResponseJson = authResult.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(authResponseJson, AuthResponse.class);
        String token = authResponse.getToken();

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void authorizeUser_Fail() throws Exception {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail(user1.getEmail());
        authRequest.setPassword("passwordWronng");

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }


}