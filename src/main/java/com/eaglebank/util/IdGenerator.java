package com.eaglebank.util;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

    private static final String USER_ID_PREFIX = "usr-";
    private static final String ACCOUNT_NUMBER_PREFIX = "01"; // As per spec, starts with 01
    private static final String TRANSACTION_ID_PREFIX = "tan-";

    private static final AtomicLong accountNumberCounter = new AtomicLong(1000000); // Start from 01000000

    public static String generateUserId() {
        return USER_ID_PREFIX + UUID.randomUUID().toString().substring(0, 8); // Shortened UUID for examples
    }

    public static String generateAccountNumber() {
        // Generate a 6-digit number and append to "01"
        return ACCOUNT_NUMBER_PREFIX + String.format("%06d", accountNumberCounter.getAndIncrement());
    }

    public static String generateTransactionId() {
        return TRANSACTION_ID_PREFIX + UUID.randomUUID().toString().substring(0, 8); // Shortened UUID for examples
    }
}