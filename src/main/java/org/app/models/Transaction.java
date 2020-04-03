package org.app.models;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Transaction {
    private BigDecimal amount;
    private ZonedDateTime timestamp;

    public Transaction(
            @JsonProperty("amount") String amount,
            @JsonProperty("timestamp") String timestamp) {
        this.amount = BigDecimal.valueOf(Double.valueOf(amount));
        this.timestamp = ZonedDateTime.parse(timestamp);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }
}
