package com.eaglebank.util;

import com.eaglebank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {

    @Autowired
    private UserService userService;

    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new com.eaglebank.exception.UnauthorizedException();
        }
        // Assuming your UserDetailsImpl stores the userId as the username
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();

        return userService.getUserByEmail(email).getId();
    }
}
