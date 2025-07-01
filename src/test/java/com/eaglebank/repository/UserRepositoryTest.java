package com.eaglebank.repository;

import com.eaglebank.entity.Address;
import com.eaglebank.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class UserRepositoryTest {
    @Container
    private static final PostgreSQLContainer<?> CONTAINER = new PostgreSQLContainer<>("postgres");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", CONTAINER::getUsername);
        registry.add("spring.datasource.password", CONTAINER::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("usr-repo-test");
        user.setName("Repo Test User");
        user.setEmail("repo.test@example.com");
        user.setPhoneNumber("+19876543210");
        user.setAddress(new Address("1 Repo Ln", null, null, "Repo City", "Repo State", "RP1 1RP"));
        user.setPassword("hashedPassword"); // In a real app, this would be hashed
        user.setCreatedTimestamp(LocalDateTime.now());
        user.setUpdatedTimestamp(LocalDateTime.now());
        userRepository.save(user);
    }

    @Test
    void findByEmail_UserFound() {
        Optional<User> foundUser = userRepository.findByEmail("repo.test@example.com");
        assertTrue(foundUser.isPresent());
        assertEquals(user.getId(), foundUser.get().getId());
    }

    @Test
    void findByEmail_UserNotFound() {
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");
        assertFalse(foundUser.isPresent());
    }

    @Test
    void saveUser() {
        User newUser = new User();
        newUser.setId("usr-new-repo");
        newUser.setName("New Repo User");
        newUser.setEmail("new.repo@example.com");
        newUser.setPhoneNumber("+11122233344");
        newUser.setAddress(new Address("2 New St", null, null, "New Town", "New County", "NW2 2NW"));
        newUser.setPassword("anotherHashedPassword");
        newUser.setCreatedTimestamp(LocalDateTime.now());
        newUser.setUpdatedTimestamp(LocalDateTime.now());

        User savedUser = userRepository.save(newUser);
        assertNotNull(savedUser.getId());
        assertEquals("New Repo User", savedUser.getName());
    }

    @Test
    void findById_UserFound() {
        Optional<User> foundUser = userRepository.findById("usr-repo-test");
        assertTrue(foundUser.isPresent());
        assertEquals("Repo Test User", foundUser.get().getName());
    }

    @Test
    void deleteUser() {
        userRepository.delete(user);
        Optional<User> deletedUser = userRepository.findById("usr-repo-test");
        assertFalse(deletedUser.isPresent());
    }
}