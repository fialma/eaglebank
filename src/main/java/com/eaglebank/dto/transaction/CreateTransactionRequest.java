package com.eaglebank.dto.transaction;

import com.eaglebank.entity.Transaction;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateTransactionRequest {
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @DecimalMax(value = "10000.00", message = "Amount cannot exceed 10000.00")
    private BigDecimal amount;

    @NotNull(message = "Currency is required")
    private Transaction.Currency currency;

    @NotNull(message = "Transaction type is required")
    private Transaction.TransactionType type;

    private String reference;
}