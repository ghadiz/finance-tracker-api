package com.ghady.financetracker.account;

import com.ghady.financetracker.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts(
            @AuthenticationPrincipal User user){
        return ResponseEntity.ok(accountService.getAllAccounts(user));
    }


    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @AuthenticationPrincipal User user,
            @RequestBody CreateAccountRequest request){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(accountService.createAccount(user,request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id){
        return ResponseEntity.ok(accountService.getAccountById(user,id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponse> updateAccount(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody UpdateAccountRequest request) {
        return ResponseEntity.ok(accountService.updateAccount(user, id , request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        accountService.deleteAccount(user , id);
        return ResponseEntity.noContent().build();
    }



}
