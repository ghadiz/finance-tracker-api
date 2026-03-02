package com.ghady.financetracker.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        Long accountId,
        BigDecimal amount,
        TransactionType type,
        String category,
        LocalDate date,
        String note,
        LocalDateTime createdAt
) {
}
