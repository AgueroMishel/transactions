package org.app.models;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Observable;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.app.TransactionsHandler;

public class Transaction extends Observable implements Runnable {
    private BigDecimal amount;
    private ZonedDateTime timestamp;

    public Transaction(
            @JsonProperty("amount") String amount,
            @JsonProperty("timestamp") String timestamp) {
        this.amount = BigDecimal.valueOf(Double.valueOf(amount));
        this.timestamp = ZonedDateTime.parse(timestamp);
        addObserver(TransactionsHandler.getInstance());
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void startAutoDeprecate() {
        new Thread(this, getName()).start();
    }

    @Override
    public synchronized void run() {
        long sleepTime =  getSleepTime();

        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            System.out.println("Transaction: '" + getName() + "' INTERRUPTED!");
        }

        setChanged();
        notifyObservers(this);
    }

    private String getName() {
        return amount + " / " + timestamp;
    }

    private long getSleepTime() {
        ZonedDateTime validTime = timestamp.plusMinutes(1);
        ZonedDateTime now = ZonedDateTime.now();

        return ChronoUnit.MILLIS.between(now, validTime);
    }
}
