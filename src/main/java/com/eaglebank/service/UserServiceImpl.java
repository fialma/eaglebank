package com.eaglebank.service;

import com.eaglebank.dto.user.CreateUserRequest;
import com.eaglebank.dto.user.UpdateUserRequest;
import com.eaglebank.dto.user.UserResponse;
import com.eaglebank.entity.Address;
import com.eaglebank.entity.User;
import com.eaglebank.exception.UserHasAccountsException;
import com.eaglebank.exception.UserNotFoundException;
import com.eaglebank.exception.UserWithEmailNotFoundException;
import com.eaglebank.exception.UserWithSameEmailAlreadyExistException;
import com.eaglebank.repository.AccountRepository;
import com.eaglebank.repository.UserRepository;
import com.eaglebank.util.IdGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    public UserServiceImpl(UserRepository userRepository, AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> { throw new UserWithSameEmailAlreadyExistException(); });

        User user = new User();
        user.setId(IdGenerator.generateUserId());
        user.setName(request.getName());

        user.setAddress(objectMapper.convertValue(request.getAddress(), Address.class));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Hash the password
        user.setCreatedTimestamp(LocalDateTime.now());
        user.setUpdatedTimestamp(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    public UserResponse getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return mapToUserResponse(user);
    }

    public UserResponse getUserByEmail(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserWithEmailNotFoundException(email));
        return mapToUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(String userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Optional.ofNullable(request.getName()).ifPresent(user::setName);
        Optional.ofNullable(request.getAddress())
                .ifPresent(addressDto -> user.setAddress(objectMapper.convertValue(addressDto, Address.class)));        Optional.ofNullable(request.getPhoneNumber()).ifPresent(user::setPhoneNumber);
        Optional.ofNullable(request.getEmail()).ifPresent(user::setEmail);
        user.setUpdatedTimestamp(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (accountRepository.countByUser(user) > 0) {
            throw new UserHasAccountsException();
        }

        userRepository.delete(user);
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setAddress(user.getAddress());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setEmail(user.getEmail());
        response.setCreatedTimestamp(user.getCreatedTimestamp());
        response.setUpdatedTimestamp(user.getUpdatedTimestamp());
        return response;
    }
}