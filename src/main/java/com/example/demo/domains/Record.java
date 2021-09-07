package com.example.demo.domains;

import java.time.LocalDate;
import java.util.Set;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Model persisted in Mongo database.
 */
@Data
@Document(collection = "records")
public class Record {

    @Id
    private String id;

    private String datasource;

    private Set<String> campaigns;

    private LocalDate daily;

    private Long clicks;

    private Long impressions;

    @Builder
    public Record(String id, String datasource, Set<String> campaigns, LocalDate localDate, Long clicks,
                  Long impressions) {
        this.id = id;
        this.datasource = datasource;
        this.campaigns = campaigns;
        this.daily = localDate;
        this.clicks = clicks;
        this.impressions = impressions;
    }

    public Record() {
    }
}
