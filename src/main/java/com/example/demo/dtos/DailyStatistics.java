package com.example.demo.dtos;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * API Model for the daily aggregated statistics.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DailyStatistics {

    private Long clicks;
    private Long impressions;
    private BigDecimal ctr;
    private LocalDate date;

    public DailyStatistics() {
    }

    public DailyStatistics(StatisticsMongoResponse dbResponse) {
        this.clicks = dbResponse.getClicks();
        this.impressions = dbResponse.getImpressions();
        this.ctr = BigDecimal.valueOf(clicks).divide(BigDecimal.valueOf(impressions), MathContext.DECIMAL32);
        this.date = dbResponse.getDate();
    }

}
