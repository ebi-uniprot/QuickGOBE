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

/**
 * Created 14/08/17
 * @author Edd
 */
@ActiveProfiles("stats-type-limit-properties-test")
@SpringBootTest(classes = StatisticsDownloadTypeReadPropertiesIT.FakeApplication.class)
class StatisticsDownloadTypeReadPropertiesIT {
    private static final String GO_ID = "goId";
    private static final String TAXON_ID = "taxonId";

    @Autowired
    private RequiredStatisticsProvider requiredStatisticsProvider;
    private List<RequiredStatistic> statistics;

    @BeforeEach
    void setUp() {
        statistics = requiredStatisticsProvider.getDownloadUsage();
    }

    @Test
    void checkLimitsReadAndSetForCorrectTypes() {
        for (RequiredStatistic request : statistics) {
            for (RequiredStatisticType type : request.getTypes()) {
                switch (type.getName()) {
                    case GO_ID:
                        // value read from yml
                        assertThat(type.getLimit(), is(500));
                        break;
                    case TAXON_ID:
                        // value read from yml
                        assertThat(type.getLimit(), is(50));
                        break;
                    default:
                        assertThat(type.getLimit(), is(DEFAULT_LIMIT));
                        break;
                }
            }
        }
    }

    @Profile("stats-type-limit-properties-test")
    @Configuration
    @EnableAutoConfiguration
    @Import(StatisticsServiceConfig.class)
    public static class FakeApplication {}
}
