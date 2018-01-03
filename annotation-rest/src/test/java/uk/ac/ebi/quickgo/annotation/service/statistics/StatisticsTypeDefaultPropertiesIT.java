package uk.ac.ebi.quickgo.annotation.service.statistics;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.annotation.service.statistics.RequiredStatisticType.DEFAULT_LIMIT;
import static uk.ac.ebi.quickgo.annotation.service.statistics.RequiredStatistics.DEFAULT_GO_TERM_LIMIT;

/**
 * Created 14/08/17
 * @author Edd
 */
@ActiveProfiles("stats-no-type-limit-properties-test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StatisticsTypeDefaultPropertiesIT.FakeApplication.class)
public class StatisticsTypeDefaultPropertiesIT {
    private static final String GO_ID = "goId";

    @Autowired
    private RequiredStatisticsProvider requiredStatisticsProvider;
    private List<RequiredStatistic> statistics;

    @Before
    public void setUp() {
        statistics = requiredStatisticsProvider.getStandardUsage();
    }

    @Test
    public void checkLimitsNotReadAndSetDefaultsForCorrectTypes() {
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
