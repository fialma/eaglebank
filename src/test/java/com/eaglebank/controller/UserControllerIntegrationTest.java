package com.eaglebank.controller;

import com.eaglebank.dto.auth.AuthRequest;
import com.eaglebank.dto.auth.AuthResponse;
import com.eaglebank.dto.user.CreateUserRequest;
import com.eaglebank.dto.user.UpdateUserRequest;
import com.eaglebank.entity.Address;
import com.eaglebank.entity.User;
import com.eaglebank.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Use a dedicated test profile for H2
@Transactional // Rollback transactions after each test
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String user1Id;
    private String user2Id;
    private String user1Token;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll(); // Clean up before each test

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
        this.user1Id = user1.getId();

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
    void createUser_Success() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName("New Test User");
        createUserRequest.setEmail("newtest@example.com");
        createUserRequest.setPhoneNumber("+447000000000");
        createUserRequest.setAddress(new Address("1 New St", null, null, "New City", "New County", "NE1 1NE"));
        createUserRequest.setPassword("newpassword");

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Test User"))
                .andExpect(jsonPath("$.email").value("newtest@example.com"));

        // Verify user is in database
        assertTrue(userRepository.findByEmail("newtest@example.com").isPresent());
    }

    @Test
    void createUser_MissingRequiredData() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName("Invalid User");
        // Missing email, phoneNumber, address, password

        mockMvc.perform(post("/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void fetchUserByID_Success() throws Exception {
        mockMvc.perform(get("/v1/users/{userId}", user1Id)
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user1Id))
                .andExpect(jsonPath("$.name").value("Integration Test User 1"));
    }

    @Test
    void fetchUserByID_Forbidden() throws Exception {
        mockMvc.perform(get("/v1/users/{userId}", user2Id) // Try to fetch another user's details
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You are not allowed to access this user's details."));
    }

    @Test
    void fetchUserByID_NotFound() throws Exception {
        mockMvc.perform(get("/v1/users/{userId}", "usr-nonexistent")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with ID: usr-nonexistent"));
    }

    @Test
    void updateUserByID_Success() throws Exception {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("Updated Test Name");
        updateUserRequest.setPhoneNumber("+447999888777");

        mockMvc.perform(patch("/v1/users/{userId}", user1Id)
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user1Id))
                .andExpect(jsonPath("$.name").value("Updated Test Name"))
                .andExpect(jsonPath("$.phoneNumber").value("+447999888777"));

        // Verify update in database
        User updatedUser = userRepository.findById(user1Id).get();
        assertEquals("Updated Test Name", updatedUser.getName());
        assertEquals("+447999888777", updatedUser.getPhoneNumber());
    }
}