package com.ghady.financetracker.budget;

import java.math.BigDecimal;

public record CreateBudgetRequest(String category, BigDecimal monthlyLimit) {
}
