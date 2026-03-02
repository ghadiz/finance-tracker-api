package com.ghady.financetracker.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountIdOrderByDateDesc(Long accountId);

    List<Transaction> findByAccountIdAndCategoryOrderByDateDesc(Long accountId, String category);

    Optional<Transaction> findByIdAndAccountUserId(Long id, Long userId);


}
