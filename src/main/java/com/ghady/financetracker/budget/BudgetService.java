package com.ghady.financetracker.budget;


import com.ghady.financetracker.exception.BadRequestException;
import com.ghady.financetracker.exception.ResourceNotFoundException;
import com.ghady.financetracker.transaction.TransactionRepository;
import com.ghady.financetracker.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    private BudgetResponse toResponse(Budget budget, Long userId){
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(
                now.getMonth().length(now.isLeapYear())
        );

        BigDecimal spent = transactionRepository
                .sumExpensesByUserAndCategoryAndDateBetween(
                        userId,
                        budget.getCategory(),
                        startOfMonth,
                        endOfMonth
                );

        BigDecimal remaining = budget.getMonthlyLimit().subtract(spent);

        boolean exceeded = spent.compareTo(budget.getMonthlyLimit()) >=0 ;

        return new BudgetResponse(
                budget.getId(),
                budget.getCategory(),
                budget.getMonthlyLimit(),
                spent,
                remaining,
                exceeded,
                budget.getCreatedAt()
        );

    }

    public BudgetResponse createBudget(User user, CreateBudgetRequest request){
        if(budgetRepository.existsByUserIdAndCategory(
                user.getId(), request.category())){
            throw new BadRequestException(
                    "Budget already exists for category: " + request.category()
            );
        }

        Budget budget = Budget.builder()
                .user(user)
                .category(request.category())
                .monthlyLimit(request.monthlyLimit())
                .build();

        Budget saved = budgetRepository.save(budget);
        return toResponse(saved, user.getId());
    }

    public List<BudgetResponse> getAllBudget(User user){
        return budgetRepository.findByUserId(user.getId())
                .stream()
                .map(budget -> toResponse(budget, user.getId()))
                .toList();
    }

    @Transactional
    public BudgetResponse updateBudget(User user, Long budgetId, UpdateBudgetRequest request){
        Budget budget = budgetRepository
                .findByIdAndUserId(budgetId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Budget not found with id: " + budgetId
                ));

        budget.setMonthlyLimit(request.monthlyLimit());
        return toResponse(budget, user.getId());
    }

    @Transactional
    public void deleteBudget(User user, Long budgetId) {
        if (budgetRepository.findByIdAndUserId(budgetId, user.getId()).isEmpty()) {
            throw new ResourceNotFoundException(
                    "Budget not found with id: " + budgetId
            );
        }
        budgetRepository.deleteById(budgetId);
    }

}
