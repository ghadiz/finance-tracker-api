package com.ghady.financetracker.budget;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BudgetResponse(
        Long id,
        String category,
        BigDecimal monthlyLimit,
        BigDecimal spent,
        BigDecimal remaining,
        boolean exceeded,
        LocalDateTime createdAt
) {
}
