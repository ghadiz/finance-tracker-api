package com.ghady.financetracker.budget;

import java.math.BigDecimal;

public record UpdateBudgetRequest(BigDecimal monthlyLimit) {
}
