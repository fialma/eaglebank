package com.eaglebank.service;

import com.eaglebank.dto.user.CreateUserRequest;
import com.eaglebank.dto.user.UpdateUserRequest;
import com.eaglebank.dto.user.UserResponse;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);
    UserResponse getUserById(String userId);
    UserResponse getUserByEmail(String email);
    UserResponse updateUser(String userId, UpdateUserRequest request);
    void deleteUser(String userId);
}
