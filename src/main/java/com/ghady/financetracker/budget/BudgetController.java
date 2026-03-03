package com.ghady.financetracker.budget;

import com.ghady.financetracker.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(
            @AuthenticationPrincipal User user,
            @RequestBody CreateBudgetRequest request
    ){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(budgetService.createBudget(user,request));
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getAllBudgets(
            @AuthenticationPrincipal User user){
        return ResponseEntity.ok(budgetService.getAllBudget(user));

    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody UpdateBudgetRequest request){
        return ResponseEntity.ok(budgetService.updateBudget(user,id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        budgetService.deleteBudget(user, id);
        return ResponseEntity.noContent().build();
    }
}
