package com.example.demo.dtos;

import java.time.LocalDate;

import lombok.Value;

/**
 * DB Model for the data returned from the aggregated mongo query
 */
@Value
public class StatisticsMongoResponse {

    private final Long clicks;
    private final Long impressions;
    private final LocalDate date;
}
