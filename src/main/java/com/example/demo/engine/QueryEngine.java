package com.example.demo.engine;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.example.demo.dtos.AggregatedStatistics;
import com.example.demo.dtos.DailyStatistics;
import com.example.demo.dtos.StatisticsMongoResponse;
import com.example.demo.dtos.StatisticsSearchParams;
import com.example.demo.exceptions.WarehouseException;
import com.example.demo.repositories.RecordRepository;
import com.google.common.base.Stopwatch;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

/**
 * Query the data based on provided params.
 */
@Service
@AllArgsConstructor
@Slf4j
public class QueryEngine {

    private static final String DAILY = "daily";
    private static final String CLICKS = "clicks";
    private static final String CAMPAIGNS = "campaigns";
    private static final String IMPRESSIONS = "impressions";
    private static final String DATASOURCE = "datasource";
    private static final String RECORDS = "records";
    private static final int MAX_ELEMENTS = 100;
    private static final String DATE = "date";
    private final RecordRepository recordRepository;
    private final MongoTemplate mongoTemplate;

    /**
     * Search for data based on provided params. Returns aggregated data report.
     */
    public AggregatedStatistics queryAggregatedStatistics(StatisticsSearchParams searchParams) {
        log.info("Query aggregated statistics {}", searchParams);
        Stopwatch stopwatch = Stopwatch.createStarted();

        // Build criteria
        Criteria criteria = buildCriteria(searchParams);

        // Query data
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                group().sum(IMPRESSIONS).as(IMPRESSIONS).sum(CLICKS).as(CLICKS),
                project(IMPRESSIONS, CLICKS),
                limit(MAX_ELEMENTS));

        // Build response
        List<StatisticsMongoResponse> responses = mongoTemplate
                .aggregate(aggregation, RECORDS, StatisticsMongoResponse.class)
                .getMappedResults();;
        stopwatch.stop();

        if(responses.isEmpty()){
            throw new WarehouseException("No results returned");
        }

        log.info("Query took {} milis to get {} results", stopwatch.elapsed(TimeUnit.MILLISECONDS), null);
        return new AggregatedStatistics(responses.get(0), searchParams);
    }

    /**
     * Search for data based on provided params. Returns aggregated daily data reports.
     */
    public List<DailyStatistics> queryDailyStatistics(StatisticsSearchParams searchParams) {
        log.info("Query daily statistics {}", searchParams);
        Stopwatch stopwatch = Stopwatch.createStarted();

        // Build criteria
        Criteria criteria = buildCriteria(searchParams);

        // Query data
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                group(DAILY).sum(IMPRESSIONS).as(IMPRESSIONS).sum(CLICKS).as(CLICKS),
                project(IMPRESSIONS, CLICKS).andExpression("_id").as(DATE),
                limit(MAX_ELEMENTS),
                sort(Sort.by(Sort.Direction.DESC, DATE)));

        // Build response
        List<StatisticsMongoResponse> responses = mongoTemplate
                .aggregate(aggregation, RECORDS, StatisticsMongoResponse.class)
                .getMappedResults();;
        stopwatch.stop();

        log.info("Query daily statics took {} milis", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        return responses.stream()
                .map(DailyStatistics::new)
                .collect(Collectors.toList());
    }

    private Criteria buildCriteria(StatisticsSearchParams searchParams) {
        Criteria criteria = new Criteria();
        List<Criteria> criterias = Lists.newArrayList();
        if (searchParams.getDatasource() != null && !searchParams.getDatasource().isEmpty()) {
            criterias.add(Criteria.where(DATASOURCE).in(decode(searchParams.getDatasource())));
        }
        if (searchParams.getCampaigns() != null && !searchParams.getCampaigns().isEmpty()) {
            criterias.add(Criteria.where(CAMPAIGNS).in(decode(searchParams.getCampaigns())));
        }
        if (searchParams.getFrom() != null) {
            criterias.add(Criteria.where(DAILY).gte(searchParams.getFrom()));
        }
        if (searchParams.getTo() != null) {
            criterias.add(Criteria.where(DAILY).lte(searchParams.getTo()));
        }
        criteria.andOperator(criterias);
        return criteria;
    }

    /**
     * Handle whitespaces e.g. "%20" -> " "
     */
    private static Set<String> decode(Set<String> urls) {
        return urls.stream().map(QueryEngine::getDecoded).collect(Collectors.toSet());
    }

    @SneakyThrows
    private static String getDecoded(String el){
        return URLDecoder.decode(el, StandardCharsets.UTF_8.toString());
    }
}
