package com.example.demo.engine;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Set;

import com.example.demo.domains.Record;
import com.example.demo.repositories.RecordRepository;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Load the data from csv file into in-memory database.
 */
@Component
@Slf4j
@AllArgsConstructor
public class DataLoader implements ApplicationRunner {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yy");
    private static final String FILE_NAME = "data.csv";
    private static final String COMMA_DELIMITER = ",";
    private static final String WHITESPACE = " ";
    private final RecordRepository recordRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Start importing data from file {}", FILE_NAME);
        InputStream is = getClass().getClassLoader().getResourceAsStream(FILE_NAME);
        InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
        try (BufferedReader br = new BufferedReader(streamReader)) {
            br.readLine(); // skip the first line
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    readSingleLine(line);
                } catch (Exception e) {
                    log.warn("Error reading the line {}. Skipping that line", line);
                }
            }
        }
        log.info("Finished importing data");
    }

    private void readSingleLine(String line) {
        String[] values = line.split(COMMA_DELIMITER);
        String datasource = values[0];

        Set<String> campaigns = Sets.newHashSet();
        campaigns.addAll(Arrays.asList(values[1].split(WHITESPACE)));

        LocalDate localDate = LocalDate.parse(values[2], DATE_FORMATTER);
        Long clicks = Long.valueOf(values[3]);
        Long impressions = Long.valueOf(values[4]);
        Record record = Record.builder()
                .datasource(datasource)
                .localDate(localDate)
                .clicks(clicks)
                .impressions(impressions)
                .campaigns(campaigns)
                .build();

        recordRepository.save(record);
    }
}
