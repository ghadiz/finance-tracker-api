package com.ghady.financetracker.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        Long id,
        String name,
        AccountType type,
        BigDecimal balance,
        LocalDateTime createdAt
) {
}
