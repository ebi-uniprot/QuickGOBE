package uk.ac.ebi.quickgo.annotation.service.statistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.annotation.service.statistics.RequiredStatisticType.DEFAULT_LIMIT;
import static uk.ac.ebi.quickgo.annotation.service.statistics.RequiredStatistics.DEFAULT_GO_TERM_LIMIT;

/**
 * Created 14/08/17
 * @author Edd
 */
@ActiveProfiles("stats-no-type-limit-properties-test")
@SpringBootTest(classes = StatisticsTypeDefaultPropertiesIT.FakeApplication.class)
class StatisticsTypeDefaultPropertiesIT {
    private static final String GO_ID = "goId";

    @Autowired
    private RequiredStatisticsProvider requiredStatisticsProvider;
    private List<RequiredStatistic> statistics;

    @BeforeEach
    void setUp() {
        statistics = requiredStatisticsProvider.getStandardUsage();
    }

    @Test
    void checkLimitsNotReadAndSetDefaultsForCorrectTypes() {
        for (RequiredStatistic request : statistics) {
            for (RequiredStatisticType  type : request.getTypes()) {
                switch (type.getName()) {
                    case GO_ID:
                        assertThat(type.getLimit(), is(DEFAULT_GO_TERM_LIMIT));
                        break;
                    default:
                        assertThat(type.getLimit(), is(DEFAULT_LIMIT));
                        break;
                }
            }
        }
    }

    @Profile("stats-no-type-limit-properties-test")
    @Configuration
    @EnableAutoConfiguration
    @Import(StatisticsServiceConfig.class)
    public static class FakeApplication {}
}
