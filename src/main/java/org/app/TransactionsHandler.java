package org.app;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.app.models.Statistics;
import org.app.models.Transaction;

@Service
public class TransactionsHandler {
    private static TransactionsHandler instance;
    private static ConcurrentHashMap<Integer,Transaction> storage;
    private static int nextKey = 0;
    private static Statistics statistics;

    static {
        instance = new TransactionsHandler();
        storage = new ConcurrentHashMap<>();
    }

    private TransactionsHandler() {}

    public static TransactionsHandler getInstance() {
        return instance;
    }

    public ConcurrentHashMap<Integer,Transaction> getAllTransactions() {
        return storage;
    }

    public synchronized ResponseEntity addSingleTransaction(Transaction transaction) {
        ZonedDateTime timestamp = ZonedDateTime.now();

        if(transaction.getTimestamp().isAfter(timestamp)) {
            return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
        } else if(transaction.getTimestamp().isBefore(timestamp.minusMinutes(1))) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } else {
            storage.put(nextKey, transaction);
            nextKey++;
        }

        return new ResponseEntity(HttpStatus.CREATED);
    }

    public ResponseEntity deleteAllTransactions() {
        storage.clear();
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Scheduled(fixedDelay = 1)
    public synchronized void createStatistics() {
        deprecateTransactions();
        if(storage.size() > 0) {
            calculateStatistics();
        } else {
            statistics = new Statistics();
        }
    }

    private synchronized void deprecateTransactions() {
        ZonedDateTime timestamp = ZonedDateTime.now();
        int currentKey;
        Transaction currentTransaction;

        for(Map.Entry<Integer,Transaction> currentEntry : storage.entrySet()) {
            currentKey = currentEntry.getKey();
            currentTransaction = currentEntry.getValue();

            if(currentTransaction.getTimestamp().isBefore(timestamp.minusMinutes(1))) {
                storage.remove(currentKey);
            }
        }
    }

    private synchronized void calculateStatistics() {
        Transaction currentTransaction;
        BigDecimal currentAmount;

        BigDecimal sum = BigDecimal.valueOf(0);
        BigDecimal avg;
        BigDecimal max = null;
        BigDecimal min = null;
        long count;

        for(Map.Entry<Integer,Transaction> currentEntry : storage.entrySet()) {
            currentTransaction = currentEntry.getValue();
            currentAmount = currentTransaction.getAmount();

            if(max == null && min == null) {
                max = currentAmount;
                min = currentAmount;
            } else if(max.compareTo(currentAmount) == -1) {
                max = currentAmount;
            } else if(min.compareTo(currentAmount) == 1) {
                min = currentAmount;
            }

            sum = sum.add(currentAmount);
        }

        count = storage.size();
        sum = sum.setScale(2);
        avg = sum.divide(BigDecimal.valueOf(count), RoundingMode.HALF_EVEN);

        statistics = new Statistics(sum, avg, max, min, count);
    }

    public synchronized Statistics getStatistics() {
        createStatistics();

        return statistics;
    }
}
