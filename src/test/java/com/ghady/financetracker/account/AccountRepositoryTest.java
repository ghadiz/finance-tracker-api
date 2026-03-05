package com.ghady.financetracker.account;

import com.ghady.financetracker.user.User;
import com.ghady.financetracker.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisabledOnOs(OS.WINDOWS)
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("financetracker_test")
            .withUsername("postgres")
            .withPassword("postgres");


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",      postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled",      () -> "true");
    }

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {

        accountRepository.deleteAll();
        userRepository.deleteAll();

        user = userRepository.save(User.builder()
                .name("Ghady")
                .email("ghady@example.com")
                .passwordHash("hashedpassword")
                .build());
    }

    @Test
    void findByUserId_shouldReturnAccountsForUser() {
        // ARRANGE — save two accounts for our user
        accountRepository.save(Account.builder()
                .user(user)
                .name("Main Checking")
                .type(AccountType.CHECKING)
                .balance(BigDecimal.valueOf(1000))
                .build());

        accountRepository.save(Account.builder()
                .user(user)
                .name("Savings")
                .type(AccountType.SAVINGS)
                .balance(BigDecimal.valueOf(500))
                .build());

        List<Account> accounts = accountRepository.findByUserId(user.getId());

        assertThat(accounts).hasSize(2);
        assertThat(accounts).extracting(Account::getName)
                .containsExactlyInAnyOrder("Main Checking", "Savings");
    }

    @Test
    void findByIdAndUserId_shouldReturnAccount_whenOwnerMatches() {
        Account saved = accountRepository.save(Account.builder()
                .user(user)
                .name("Main Checking")
                .type(AccountType.CHECKING)
                .balance(BigDecimal.valueOf(1000))
                .build());

        Optional<Account> found = accountRepository.findByIdAndUserId(saved.getId(), user.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Main Checking");
    }

    @Test
    void findByIdAndUserId_shouldReturnEmpty_whenOwnerDoesNotMatch() {
        Account saved = accountRepository.save(Account.builder()
                .user(user)
                .name("Main Checking")
                .type(AccountType.CHECKING)
                .balance(BigDecimal.valueOf(1000))
                .build());

        Optional<Account> found = accountRepository.findByIdAndUserId(saved.getId(), 999L);

        assertThat(found).isEmpty();
    }

    @Test
    void existsByIdAndUserId_shouldReturnTrue_whenOwnerMatches() {
        Account saved = accountRepository.save(Account.builder()
                .user(user)
                .name("Main Checking")
                .type(AccountType.CHECKING)
                .balance(BigDecimal.valueOf(1000))
                .build());

        boolean exists = accountRepository.existsByIdAndUserId(saved.getId(), user.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void existsByIdAndUserId_shouldReturnFalse_whenOwnerDoesNotMatch() {
        Account saved = accountRepository.save(Account.builder()
                .user(user)
                .name("Main Checking")
                .type(AccountType.CHECKING)
                .balance(BigDecimal.valueOf(1000))
                .build());

        boolean exists = accountRepository.existsByIdAndUserId(saved.getId(), 999L);

        assertThat(exists).isFalse();
    }
}