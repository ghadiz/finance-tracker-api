package com.ghady.financetracker.transaction;

import com.ghady.financetracker.account.Account;
import com.ghady.financetracker.account.AccountRepository;
import com.ghady.financetracker.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    private TransactionResponse toResponse(Transaction transaction){
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAccount().getId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getCategory(),
                transaction.getDate(),
                transaction.getNote(),
                transaction.getCreatedAt()
        );
    }

    private void applyToBalance(Account account, TransactionType type, java.math.BigDecimal amount){
        if (type == TransactionType.INCOME){
            account.setBalance(account.getBalance().add(amount));
        } else{
            account.setBalance(account.getBalance().subtract(amount));
        }
    }

    private void reverseFromBalance(Account account, TransactionType type, java.math.BigDecimal amount){
        if(type == TransactionType.INCOME){
            account.setBalance(account.getBalance().subtract(amount));
        } else {
            account.setBalance(account.getBalance().add(amount));
        }
    }

    public TransactionResponse createTransaction(User user, CreateTransactionRequest request){
        Account account = accountRepository
                .findByIdAndUserId(request.accountId(), user.getId())
                .orElseThrow(() -> new RuntimeException("Account not found with id : " +request.accountId()));

        Transaction transaction = Transaction.builder()
                .account(account)
                .amount(request.amount())
                .type(request.type())
                .category(request.category())
                .date(request.date())
                .note(request.note())
                .build();

        applyToBalance(account, request.type(), request.amount());

        accountRepository.save(account);
        Transaction saved = transactionRepository.save(transaction);

        return toResponse(saved);

    }

    public List<TransactionResponse> getTransactions(User user, Long accountId){
        accountRepository.findByIdAndUserId(accountId, user.getId())
                .orElseThrow(() -> new RuntimeException(
                        "Account not found with id: " + accountId
                ));

        return transactionRepository
                .findByAccountIdOrderByDateDesc(accountId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public  TransactionResponse getTransactionById(User user, Long transactionId){
        return transactionRepository
                .findByIdAndAccountUserId(transactionId, user.getId())
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException(
                        "Transaction not found with id: " + transactionId
                ));
    }

    @Transactional
    public TransactionResponse updateTransaction(User user, Long transactionId, UpdateTransactionRequest request){
        Transaction transaction = transactionRepository
                .findByIdAndAccountUserId(transactionId, user.getId())
                .orElseThrow(() -> new RuntimeException(
                        "Transaction not found with id: " + transactionId
                ));

        Account account = transaction.getAccount();

        reverseFromBalance(account, transaction.getType(), transaction.getAmount());

        transaction.setAmount(request.amount());
        transaction.setType(request.type());
        transaction.setCategory(request.category());
        transaction.setDate(request.date());
        transaction.setNote(request.note());

        applyToBalance(account, request.type(), request.amount());

        accountRepository.save(account);

        return toResponse(transaction);

    }

    @Transactional
    public void deleteTransaction(User user, Long transactionId){
        Transaction transaction = transactionRepository
                .findByIdAndAccountUserId(transactionId, user.getId())
                .orElseThrow(() -> new RuntimeException(
                        "Transaction not found with id: " + transactionId
                ));

        Account account = transaction.getAccount();

        reverseFromBalance(account, transaction.getType(), transaction.getAmount());
        accountRepository.save(account);

        transactionRepository.deleteById(transactionId);
    }

}
