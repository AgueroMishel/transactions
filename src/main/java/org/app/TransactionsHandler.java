package org.app;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.app.models.Statistics;
import org.app.models.Transaction;

@Service
public class TransactionsHandler implements Observer {
    private static TransactionsHandler instance;
    private static List<Transaction> storage;
    private static boolean statisticsCalculated = false;
    private static Statistics statistics;

    private TransactionsHandler() {}

    public static TransactionsHandler getInstance() {
        if (instance == null) {
            instance = new TransactionsHandler();
            storage = new LinkedList<>();
            statistics = new Statistics();
        }

        return instance;
    }

    public List<Transaction> getAllTransactions() {
        return storage;
    }

    public synchronized ResponseEntity addSingleTransaction(Transaction transaction) {
        ZonedDateTime timestamp = ZonedDateTime.now();

        if(transaction.getTimestamp().isAfter(timestamp)) {
            return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
        } else if(transaction.getTimestamp().isBefore(timestamp.minusMinutes(1))) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } else {
            storage.add(transaction);
            transaction.startAutoDeprecate();
            calculateStatistics(transaction, true);
        }

        return new ResponseEntity(HttpStatus.CREATED);
    }

    public ResponseEntity deleteAllTransactions() {
        storage.clear();
        clearStatistics();
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    public synchronized Statistics getStatistics() {
        return statistics;
    }

    public void calculateStatistics(Transaction transaction, boolean isAdding) {
        BigDecimal sum;
        BigDecimal avg;
        BigDecimal max;
        BigDecimal min;
        long count = storage.size();

        if(statisticsCalculated) {
            if(isAdding) {
                Collections.sort(storage, (t1, t2) -> t2.getAmount().compareTo(t1.getAmount()));
                sum = statistics.getBigDecimalSum().add(transaction.getAmount());
            } else {
                sum = statistics.getBigDecimalSum().subtract(transaction.getAmount());
            }

            max = ((LinkedList<Transaction>) storage).getFirst().getAmount();
            min = ((LinkedList<Transaction>) storage).getLast().getAmount();
        } else {
            max = transaction.getAmount();
            min = transaction.getAmount();
            sum = transaction.getAmount();

            statisticsCalculated = true;
        }

        sum = sum.setScale(2);
        avg = sum.divide(BigDecimal.valueOf(count), RoundingMode.HALF_EVEN);

        statistics = new Statistics(sum, avg, max, min, count);
    }

    public void clearStatistics() {
        statistics = new Statistics();
        statisticsCalculated = false;
    }

    @Override
    public synchronized void update(Observable obj, Object arg) {
        Transaction deprecatedTransaction = (Transaction) arg;
        storage.remove(deprecatedTransaction);
        calculateStatistics(deprecatedTransaction, false);
    }
}
