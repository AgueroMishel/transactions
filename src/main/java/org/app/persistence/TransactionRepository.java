package org.app.persistence;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import org.app.models.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    Transaction save(Transaction transaction);

    List<Transaction> findByIsDeleted(boolean isDeleted);

    @Query(value = "SELECT SUM(amount) FROM transactions WHERE is_deleted = false", nativeQuery = true)
    BigDecimal getSumAmount();

    @Query(value = "SELECT AVG(amount) FROM transactions WHERE is_deleted = false", nativeQuery = true)
    BigDecimal getAvgAmount();

    @Query(value = "SELECT MAX(amount) FROM transactions WHERE is_deleted = false", nativeQuery = true)
    BigDecimal getMaxAmount();

    @Query(value = "SELECT MIN(amount) FROM transactions WHERE is_deleted = false", nativeQuery = true)
    BigDecimal getMinAmount();

    @Query(value = "SELECT COUNT(amount) FROM transactions WHERE is_deleted = false", nativeQuery = true)
    Long getCountAmount();

    @Async
    @Transactional
    @Modifying
    @Query(value = "UPDATE transactions SET is_deleted = :isDeleted WHERE id = :id", nativeQuery = true)
    void setIsDeletedById(@Param("isDeleted")boolean isDeleted, @Param("id")int id);
}
