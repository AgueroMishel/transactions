package org.app.models;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Statistics {
    private BigDecimal sum;
    private BigDecimal avg;
    private BigDecimal max;
    private BigDecimal min;
    private long count;

    public Statistics(
            @JsonProperty("sum") BigDecimal sum,
            @JsonProperty("avg") BigDecimal avg,
            @JsonProperty("max") BigDecimal max,
            @JsonProperty("min") BigDecimal min,
            @JsonProperty("count") long count) {
        this.sum = sum;
        this.avg = avg;
        this.max = max;
        this.min = min;
        this.count = count;
    }

    public Statistics() {
        this.sum = BigDecimal.valueOf(0);
        this.avg = BigDecimal.valueOf(0);
        this.max = BigDecimal.valueOf(0);
        this.min = BigDecimal.valueOf(0);
        this.count = 0;
    }

    public String getSum() {
        return formatBigDecimal(sum);
    }

    public String getAvg() {
        return formatBigDecimal(avg);
    }

    public String getMax() {
        return formatBigDecimal(max);
    }

    public String getMin() {
        return formatBigDecimal(min);
    }

    public long getCount() {
        return count;
    }

    private String formatBigDecimal(BigDecimal number) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(number);
    }
}
