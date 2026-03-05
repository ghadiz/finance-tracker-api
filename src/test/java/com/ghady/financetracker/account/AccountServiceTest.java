package com.ghady.financetracker.account;

import com.ghady.financetracker.exception.ResourceNotFoundException;
import com.ghady.financetracker.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private User user;
    private Account account;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("ghady@example.com").name("Ghady").build();
        account = Account.builder()
                .id(1L)
                .user(user)
                .name("Main Checking")
                .type(AccountType.CHECKING)
                .balance(BigDecimal.valueOf(1000))
                .build();
    }

    @Test
    void getAllAccounts_shouldReturnAccountsForUser() {
        when(accountRepository.findByUserId(1L)).thenReturn(List.of(account));

        List<AccountResponse> result = accountService.getAllAccounts(user);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Main Checking");
        assertThat(result.get(0).balance()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    void createAccount_shouldSaveAndReturnAccount() {
        CreateAccountRequest req = new CreateAccountRequest("Savings", AccountType.SAVINGS);
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> {
            Account a = i.getArgument(0);
            // simulate DB assigning an ID
            return Account.builder().id(2L).user(user).name(a.getName()).type(a.getType()).balance(BigDecimal.ZERO).build();
        });

        AccountResponse result = accountService.createAccount(user, req);

        assertThat(result.name()).isEqualTo("Savings");
        assertThat(result.type()).isEqualTo(AccountType.SAVINGS);
        assertThat(result.balance()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void getAccountById_shouldReturnAccount_whenOwnerMatches() {
        when(accountRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(account));

        AccountResponse result = accountService.getAccountById(user, 1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Main Checking");
    }

    @Test
    void getAccountById_shouldThrow_whenAccountNotFound() {
        when(accountRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccountById(user, 99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Account not found with id: 99");
    }

    @Test
    void deleteAccount_shouldDelete_whenOwnerMatches() {
        when(accountRepository.existsByIdAndUserId(1L, 1L)).thenReturn(true);

        accountService.deleteAccount(user, 1L);

        verify(accountRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteAccount_shouldThrow_whenAccountNotFound() {
        when(accountRepository.existsByIdAndUserId(99L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> accountService.deleteAccount(user, 99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Account not found with id: 99");

        verify(accountRepository, never()).deleteById(any());
    }
}