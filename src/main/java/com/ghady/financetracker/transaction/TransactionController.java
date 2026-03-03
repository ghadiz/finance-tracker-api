package com.ghady.financetracker.transaction;


import com.ghady.financetracker.user.User;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;


    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @AuthenticationPrincipal User user,
            @RequestBody CreateTransactionRequest request){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionService.createTransaction(user, request));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @AuthenticationPrincipal User user,
            @RequestParam Long accountId){
        return ResponseEntity.ok(
                transactionService.getTransactions(user, accountId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody UpdateTransactionRequest request){
        return ResponseEntity.ok(
                transactionService.updateTransaction(user, id, request)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> udpateTranasaction(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody UpdateTransactionRequest request){
        return ResponseEntity.ok(
                transactionService.updateTransaction(user , id ,request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        transactionService.deleteTransaction(user, id);
        return ResponseEntity.noContent().build();
    }

}
