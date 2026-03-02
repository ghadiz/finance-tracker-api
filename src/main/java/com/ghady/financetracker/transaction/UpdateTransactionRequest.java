package com.ghady.financetracker.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateTransactionRequest(
        BigDecimal amount,
        TransactionType type,
        String category,
        LocalDate date,
        String note
) {
}
