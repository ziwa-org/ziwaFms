package com.example.ziwa.repository;

import com.example.ziwa.model.FinancialTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, Long> {
    
    Page<FinancialTransaction> findByDeletedFalseAndDateBetween(
        LocalDate start, 
        LocalDate end, 
        Pageable pageable
    );
    
    Page<FinancialTransaction> findByDeletedFalseAndTypeAndDateBetween(
        FinancialTransaction.TransactionType type,
        LocalDate start,
        LocalDate end,
        Pageable pageable
    );
    
    Page<FinancialTransaction> findByDeletedFalseAndCategoryAndDateBetween(
        String category,
        LocalDate start,
        LocalDate end,
        Pageable pageable
    );
    
    Page<FinancialTransaction> findByDeletedFalseAndTypeAndCategoryAndDateBetween(
        FinancialTransaction.TransactionType type,
        String category,
        LocalDate start,
        LocalDate end,
        Pageable pageable
    );
    
    @Query("SELECT SUM(ft.amount) FROM FinancialTransaction ft " +
           "WHERE ft.deleted = false AND ft.type = :type AND ft.date BETWEEN :start AND :end")
    Double sumByTypeAndDateBetween(
        @Param("type") FinancialTransaction.TransactionType type,
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );
    
    @Query("SELECT ft.category, SUM(ft.amount), COUNT(ft) " +
           "FROM FinancialTransaction ft " +
           "WHERE ft.deleted = false AND ft.type = :type AND ft.date BETWEEN :start AND :end " +
           "GROUP BY ft.category")
    List<Object[]> getBreakdownByCategory(
        @Param("type") FinancialTransaction.TransactionType type,
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );
    
    @Query("SELECT FUNCTION('YEAR', ft.date), FUNCTION('MONTH', ft.date), " +
           "SUM(CASE WHEN ft.type = 'INCOME' THEN ft.amount ELSE 0 END), " +
           "SUM(CASE WHEN ft.type = 'EXPENSE' THEN ft.amount ELSE 0 END) " +
           "FROM FinancialTransaction ft " +
           "WHERE ft.deleted = false AND ft.date BETWEEN :start AND :end " +
           "GROUP BY FUNCTION('YEAR', ft.date), FUNCTION('MONTH', ft.date) " +
           "ORDER BY FUNCTION('YEAR', ft.date), FUNCTION('MONTH', ft.date)")
    List<Object[]> getMonthlyTrends(
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );
}
