package com.ghady.financetracker.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountIdOrderByDateDesc(Long accountId);

    List<Transaction> findByAccountIdAndCategoryOrderByDateDesc(Long accountId, String category);

    Optional<Transaction> findByIdAndAccountUserId(Long id, Long userId);

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.account.user.id = :userId
            AND t.category = :category
            AND t.type = com.ghady.financetracker.transaction.TransactionType.EXPENSE
            AND t.date BETWEEN :startDate AND :endDate
            """)
    BigDecimal sumExpensesByUserAndCategoryAndDateBetween(
            @Param("userId") Long userId,
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate

    );


}
