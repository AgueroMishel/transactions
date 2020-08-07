package org.app;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.app.models.Statistics;
import org.app.models.Transaction;
import org.app.persistence.TransactionRepository;

@Service
public class TransactionsHandler implements Observer {
    private static Statistics statistics = new Statistics();

    @Autowired
    private TransactionRepository repository;

    public List<Transaction> getAllTransactions() {
        return repository.findByIsDeleted(false);
    }

    public synchronized ResponseEntity addSingleTransaction(Transaction transaction) {
        ZonedDateTime timestamp = ZonedDateTime.now();

        if (transaction.getTimestamp().isAfter(timestamp)) {
            return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
        } else if (transaction.getTimestamp().isBefore(timestamp.minusMinutes(1))) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } else {
            Transaction savedTransaction = repository.save(transaction);
            savedTransaction.startAutoDeprecate(this);
            createStatistics();
        }

        return new ResponseEntity(HttpStatus.CREATED);
    }

    public ResponseEntity deleteAllTransactions() {
        clearStatistics();
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    public synchronized Statistics getStatistics() {
        return statistics;
    }

    public void createStatistics() {
        long count = repository.getCountAmount();
        BigDecimal sum;
        BigDecimal avg;
        BigDecimal max;
        BigDecimal min;

        if (count > 0) {
            sum = repository.getSumAmount();
            avg = repository.getAvgAmount();
            max = repository.getMaxAmount();
            min = repository.getMinAmount();

            statistics = new Statistics(sum, avg, max, min, count);
        } else {
            clearStatistics();
        }
    }

    public void clearStatistics() {
        statistics = new Statistics();
    }

    @Override
    public synchronized void update(Observable obj, Object arg) {
        Transaction deprecatedTransaction = (Transaction) arg;
        repository.setIsDeletedById(true, deprecatedTransaction.getId());
        createStatistics();
    }
}
