package com.ghady.financetracker.account;

import com.ghady.financetracker.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    private AccountResponse toResponse(Account account){
        return new AccountResponse(
                account.getId(),
                account.getName(),
                account.getType(),
                account.getBalance(),
                account.getCreatedAt()
        );
    }


    public List<AccountResponse> getAllAccounts(User user){
        return accountRepository.findByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public AccountResponse  createAccount(User user, CreateAccountRequest request){
        Account newAccount = Account.builder()
                .user(user)
                .name(request.name())
                .type(request.type())
                .build();
        return toResponse(accountRepository.save(newAccount));
    }

    public AccountResponse getAccountById(User user, Long accountId){
        return accountRepository.findByIdAndUserId(accountId, user.getId())
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException(
                        "Account not found with id: " + accountId
                ));
    }

    @Transactional
    public AccountResponse updateAccount(User user, Long accountId, UpdateAccountRequest request){
        Account account = accountRepository.findByIdAndUserId(accountId, user.getId())
                .orElseThrow(() -> new RuntimeException(
                        "Account not found with id: " + accountId
                ));
        account.setName(request.name());
        return toResponse(account);
    }

    @Transactional
    public void deleteAccount(User user, Long accountId){
        if(!accountRepository.existsByIdAndUserId(accountId, user.getId())){
            throw new RuntimeException("Account not found with id: " + accountId);
        }
        accountRepository.deleteById(accountId);
    }

}
