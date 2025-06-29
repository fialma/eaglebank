package com.eaglebank.service;

import com.eaglebank.dto.auth.AuthRequest;
import com.eaglebank.dto.auth.AuthResponse;

public interface AuthService {
    AuthResponse authenticateUser(AuthRequest request);
}
