package com.example.demo.dtos;

import java.time.LocalDate;
import java.util.Set;

import lombok.Value;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * API model for the query provided by the client
 */
@Value
public class StatisticsSearchParams {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate from;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate to;

    private Set<String> campaigns;

    private Set<String> datasource;

}
