package com.eaglebank.controller;

import com.eaglebank.dto.user.CreateUserRequest;
import com.eaglebank.dto.user.UpdateUserRequest;
import com.eaglebank.dto.user.UserResponse;
import com.eaglebank.exception.UserDetailNotAllowedException;
import com.eaglebank.service.UserService;
import com.eaglebank.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    SecurityUtil securityUtil;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse userResponse = userService.createUser(request);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> fetchUserByID(@PathVariable String userId) {
        String authenticatedUserId = securityUtil.getCurrentUserId();
        if (!authenticatedUserId.equals(userId)) {
            throw new UserDetailNotAllowedException();
        }
        UserResponse userResponse = userService.getUserById(userId);
        return ResponseEntity.ok(userResponse);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUserByID(@PathVariable String userId, @Valid @RequestBody UpdateUserRequest request) {
        String authenticatedUserId = securityUtil.getCurrentUserId();
        if (!authenticatedUserId.equals(userId)) {
            throw new UserDetailNotAllowedException();
        }
        UserResponse userResponse = userService.updateUser(userId, request);
        return ResponseEntity.ok(userResponse);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<UserResponse> deleteUserByID(@PathVariable String userId) {
        String authenticatedUserId = securityUtil.getCurrentUserId();
        if (!authenticatedUserId.equals(userId)) {
            throw new UserDetailNotAllowedException();
        }
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

//    private String getCurrentUserId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated()) {
//            throw new com.eaglebank.exception.UnauthorizedException();
//        }
//        // Assuming your UserDetailsImpl stores the userId as the username
//        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
//
//        return userService.getUserByEmail(email).getId();
//    }
}