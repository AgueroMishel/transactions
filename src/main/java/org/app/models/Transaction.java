package org.app.models;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Observable;
import java.util.Observer;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Entity
@Table(name = "transactions")
@JsonPropertyOrder({"id", "amount", "timestamp", "deleted"})
public class Transaction extends Observable implements Runnable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private BigDecimal amount;
    private ZonedDateTime timestamp;
    private boolean isDeleted;

    public Transaction() {

    }

    public Transaction(
            @JsonProperty("amount") String amount,
            @JsonProperty("timestamp") String timestamp) {
        this.amount = BigDecimal.valueOf(Double.valueOf(amount));
        this.timestamp = ZonedDateTime.parse(timestamp);
        this.isDeleted = false;
    }

    public int getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void startAutoDeprecate(Observer observer) {
        addObserver(observer);
        new Thread(this, getName()).start();
    }

    @Override
    public synchronized void run() {
        long sleepTime = getSleepTime();

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
