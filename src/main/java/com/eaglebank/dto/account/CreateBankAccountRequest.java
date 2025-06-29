package com.eaglebank.dto.account;

import com.eaglebank.entity.Account;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateBankAccountRequest {
    @NotBlank(message = "Account name is required")
    private String name;

    @NotNull(message = "Account type is required")
    private Account.AccountType accountType;

    @NotBlank(message = "User ID is required")
    @Pattern(regexp = "^usr-[A-Za-z0-9]+$", message = "Invalid user ID format")
    private String userId;
}