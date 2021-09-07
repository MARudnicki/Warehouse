package com.example.demo.api;

import java.util.List;
import javax.validation.Valid;

import com.example.demo.dtos.AggregatedStatistics;
import com.example.demo.dtos.DailyStatistics;
import com.example.demo.dtos.StatisticsSearchParams;
import com.example.demo.engine.QueryEngine;
import com.example.demo.exceptions.WarehouseException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Api contains two method - for aggregated date, and daily
 */
@RestController
@AllArgsConstructor
@Slf4j
public class WarehouseApi {

    private final QueryEngine queryEngine;

    @GetMapping("/statsAggregated")
    public AggregatedStatistics getAggregatedStatistics(@Valid StatisticsSearchParams searchParams){

        if(searchParams.getTo() == null && searchParams.getFrom() == null &&
                searchParams.getDatasource().isEmpty() && searchParams.getCampaigns().isEmpty()){
            throw new WarehouseException("You have to fill some criteria");
        }

        return queryEngine.queryAggregatedStatistics(searchParams);
    }

    @GetMapping("/statsDaily")
    public List<DailyStatistics> getDailyStatistics(@Valid StatisticsSearchParams searchParams){

        if(searchParams.getFrom() == null || searchParams.getTo() == null){
            throw new WarehouseException("For daily aggregated statistics, from and to dates have to be set");
        }

        return queryEngine.queryDailyStatistics(searchParams);
    }
}
