package com.example.demo;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import com.example.demo.dtos.AggregatedStatistics;
import com.example.demo.dtos.DailyStatistics;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class ApiTests {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    /**
     * Total clicks for a given Datasource fir a given Data range.
     */
    @Test
    void datasourceAndDataRange() {

        //Arrange & Act
        AggregatedStatistics response =
                given().mockMvc(mvc)
                        .get("/statsAggregated?from=2019-10-01&to=2020-10-29&datasource=Facebook Ads")
                        .then()
                        .assertThat().statusCode(200)
                        .extract().as(AggregatedStatistics.class);
        //Assert
        assertThat(response.getClicks()).isEqualTo(331);
        assertThat(response.getImpressions()).isEqualTo(1952);
        assertThat(response.getCtr().doubleValue()).isEqualTo(0.1695697);
        assertThat(response.getDatasource()).isEqualTo(Sets.newHashSet("Facebook Ads"));
        assertThat(response.getFrom()).isEqualTo(LocalDate.of(2019, 10, 01));
        assertThat(response.getTo()).isEqualTo(LocalDate.of(2020, 10, 29));
    }

    /**
     * Click-Through Rate per Datasource and Campaign
     */
    @Test
    void datasourceAndCampaign() {

        //Arrange & Act
        AggregatedStatistics response =
                given().mockMvc(mvc)
                        .get("/statsAggregated?datasource=Facebook Ads&campaigns=Versicherungen")
                        .then()
                        .assertThat().statusCode(200)
                        .extract().as(AggregatedStatistics.class);
        //Assert
        assertThat(response.getClicks()).isEqualTo(0);
        assertThat(response.getImpressions()).isEqualTo(27);
        assertThat(response.getCtr().doubleValue()).isEqualTo(0);
        assertThat(response.getDatasource()).isEqualTo(Sets.newHashSet("Facebook Ads"));
        assertThat(response.getCampaigns()).isEqualTo(Sets.newHashSet("Versicherungen"));
    }

    /**
     * Impressions over time (daily)
     */
    @Test
    void dailyImpressions() {

        //Arrange & Act
        DailyStatistics[] response =
                given().mockMvc(mvc)
                        .get("/statsDaily?datasource=Facebook Ads&from=2019-10-01&to=2020-10-29&datasource=Facebook Ads")
                        .then()
                        .assertThat().statusCode(200)
                        .extract().as(DailyStatistics[].class);
        //Assert
        assertThat(response.length).isEqualTo(2);
        for(DailyStatistics dailyStatistics : response){
            assertThat(dailyStatistics.getClicks()).isNotNull();
            assertThat(dailyStatistics.getImpressions()).isNotNull();
            assertThat(dailyStatistics.getCtr()).isNotNull();
        }
    }

}
