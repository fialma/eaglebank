package com.eaglebank.service;

import com.eaglebank.dto.user.CreateUserRequest;
import com.eaglebank.dto.user.UpdateUserRequest;
import com.eaglebank.dto.user.UserResponse;
import com.eaglebank.entity.Address;
import com.eaglebank.entity.User;
import com.eaglebank.exception.UserHasAccountsException;
import com.eaglebank.exception.UserNotFoundException;
import com.eaglebank.repository.AccountRepository;
import com.eaglebank.repository.UserRepository;
import com.eaglebank.util.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("usr-123");
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPhoneNumber("+1234567890");
        testUser.setAddress(new Address("1 Test St", null, null, "Test Town", "Test County", "TS1 1ST"));
        testUser.setPassword("hashedPassword");
        testUser.setCreatedTimestamp(LocalDateTime.now());
        testUser.setUpdatedTimestamp(LocalDateTime.now());

        createUserRequest = new CreateUserRequest();
        createUserRequest.setName("New User");
        createUserRequest.setEmail("new.user@example.com");
        createUserRequest.setPhoneNumber("+1122334455");
        createUserRequest.setAddress(new Address("1 New St", null, null, "New Town", "New County", "NW1 1NW"));
        createUserRequest.setPassword("rawPassword");

        updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("Updated Name");
    }

    @Test
    void createUser_Success() {
        try (MockedStatic<IdGenerator> mockedStatic = Mockito.mockStatic(IdGenerator.class)) {
            mockedStatic.when(IdGenerator::generateUserId).thenReturn("usr-new");
            when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            UserResponse response = userService.createUser(createUserRequest);

            assertNotNull(response);
            assertEquals("usr-123", response.getId()); // ID from testUser mock
            assertEquals("John Doe", response.getName()); // Name from testUser mock
            verify(userRepository, times(1)).save(any(User.class));
            verify(passwordEncoder, times(1)).encode("rawPassword");
        }
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        UserResponse response = userService.getUserById(testUser.getId());

        assertNotNull(response);
        assertEquals(testUser.getId(), response.getId());
        assertEquals(testUser.getName(), response.getName());
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findById("nonExistentUser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById("nonExistentUser"));
    }

    @Test
    void updateUser_Success() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser); // Return the same user after update

        UserResponse response = userService.updateUser(testUser.getId(), updateUserRequest);

        assertNotNull(response);
        assertEquals(updateUserRequest.getName(), response.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_NotFound() {
        when(userRepository.findById("nonExistentUser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser("nonExistentUser", updateUserRequest));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(accountRepository.countByUser(testUser)).thenReturn(0L);
        doNothing().when(userRepository).delete(testUser);

        assertDoesNotThrow(() -> userService.deleteUser(testUser.getId()));
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void deleteUser_NotFound() {
        when(userRepository.findById("nonExistentUser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser("nonExistentUser"));
    }

    @Test
    void deleteUser_HasAccounts() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(accountRepository.countByUser(testUser)).thenReturn(1L);

        assertThrows(UserHasAccountsException.class, () -> userService.deleteUser(testUser.getId()));
        verify(userRepository, never()).delete(any(User.class));
    }
}