package com.example.demo.dtos;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Model for the aggregated statistics.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AggregatedStatistics {

    private Long clicks;
    private Long impressions;
    private BigDecimal ctr;
    private Set<String> datasource;
    private Set<String> campaigns;
    private LocalDate from;
    private LocalDate to;

    public AggregatedStatistics() {
    }

    public AggregatedStatistics(StatisticsMongoResponse dbResponse, StatisticsSearchParams searchParams) {
        this.clicks = dbResponse.getClicks();
        this.impressions = dbResponse.getImpressions();
        this.ctr = BigDecimal.valueOf(clicks).divide(BigDecimal.valueOf(impressions), MathContext.DECIMAL32);
        this.datasource = searchParams.getDatasource();
        this.campaigns = searchParams.getCampaigns();
        this.from = searchParams.getFrom();
        this.to = searchParams.getTo();
    }
}
