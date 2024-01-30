package uk.ac.ebi.quickgo.annotation.service.statistics;

import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Tony Wardell
 * Date: 03/01/2018
 * Time: 15:46
 * Created with IntelliJ IDEA.
 */
class RequiredStatisticsProviderTest {

    // the configured stats limits used in this test
    private StatisticsTypeConfigurer standard;
    private StatisticsTypeConfigurer download;

    @BeforeEach
    void setup() {
        standard = new StatisticsTypeConfigurer(new HashMap<>());
        download = new StatisticsTypeConfigurer(new HashMap<>());
    }

    @Test
    void statisticsTypeConfigurerForStandardUseCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () -> new RequiredStatisticsProvider(null, download));
    }

    @Test
    void statisticsTypeConfigurerForDownloadUseCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () -> new RequiredStatisticsProvider(standard, null));
    }

    @Test
    void requiredStatisticsAreBuiltWithTheCorrectLimits() {
        RequiredStatisticsProvider provider = new RequiredStatisticsProvider(standard, download);

        List<RequiredStatistic> requiredStatistics = provider.getStandardUsage();
        assertThat(requiredStatistics.get(0).getTypes(), hasSize(6));

        requiredStatistics = provider.getDownloadUsage();
        assertThat(requiredStatistics.get(0).getTypes(), hasSize(6));

        requiredStatistics = provider.getStandardUsageWithGeneProductFiltering();
        assertThat(requiredStatistics.get(0).getTypes(), hasSize(7));

        requiredStatistics = provider.getDownloadUsageWithGeneProductFiltering();
        assertThat(requiredStatistics.get(0).getTypes(), hasSize(7));
    }
}
