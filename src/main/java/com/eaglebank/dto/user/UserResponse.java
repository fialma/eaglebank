package com.eaglebank.dto.user;

import com.eaglebank.entity.Address;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private String id;
    private String name;
    private Address address;
    private String phoneNumber;
    private String email;
    private LocalDateTime createdTimestamp;
    private LocalDateTime updatedTimestamp;
}