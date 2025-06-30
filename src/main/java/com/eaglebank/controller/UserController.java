package com.eaglebank.controller;

import com.eaglebank.dto.Response;
import com.eaglebank.dto.error.ErrorResponse;
import com.eaglebank.dto.user.CreateUserRequest;
import com.eaglebank.dto.user.UpdateUserRequest;
import com.eaglebank.dto.user.UserResponse;
import com.eaglebank.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;
    private final String userNotAllowedMessage = "You are not allowed to access this user's details.";

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse userResponse = userService.createUser(request);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Response> fetchUserByID(@PathVariable String userId) {
        String authenticatedUserId = getCurrentUserId();
        if (!authenticatedUserId.equals(userId)) {
            return new ResponseEntity<>(new ErrorResponse(userNotAllowedMessage), HttpStatus.FORBIDDEN);
        }
        UserResponse userResponse = userService.getUserById(userId);
        return ResponseEntity.ok(userResponse);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Response> updateUserByID(@PathVariable String userId, @Valid @RequestBody UpdateUserRequest request) {
        String authenticatedUserId = getCurrentUserId();
        if (!authenticatedUserId.equals(userId)) {
            return new ResponseEntity<>(new ErrorResponse(userNotAllowedMessage), HttpStatus.FORBIDDEN);
        }
        UserResponse userResponse = userService.updateUser(userId, request);
        return ResponseEntity.ok(userResponse);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Response> deleteUserByID(@PathVariable String userId) {
        String authenticatedUserId = getCurrentUserId();
        if (!authenticatedUserId.equals(userId)) {
            return new ResponseEntity<>(new ErrorResponse(userNotAllowedMessage), HttpStatus.FORBIDDEN);
        }
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new com.eaglebank.exception.UnauthorizedException("User is not authenticated");
        }
        // Assuming your UserDetailsImpl stores the userId as the username
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();

        return userService.getUserByEmail(email).getId();
    }
}