package com.eaglebank.dto.auth;

import com.eaglebank.dto.Response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class AuthResponse extends Response {
    private String token;
}