package uk.ac.ebi.quickgo.annotation.service.statistics;

import java.util.List;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

/**
 * @author Tony Wardell
 * Date: 03/01/2018
 * Time: 15:46
 * Created with IntelliJ IDEA.
 */
public class RequiredStatisticsProviderTest {

    @Mock
    private StatisticsTypeConfigurer standard;
    @Mock
    private StatisticsTypeConfigurer download;

    @Test(expected = IllegalArgumentException.class)
    public void statisticsTypeConfigurerForStandardUseCannotBeNull() {
        new RequiredStatisticsProvider(null, download);
    }

    @Test(expected = IllegalArgumentException.class)
    public void statisticsTypeConfigurerForDownloadUseCannotBeNull() {
        new RequiredStatisticsProvider(standard, null);
    }

    @Test
    public void requiredStatisticsAreBuiltWithTheCorrectLimits() {
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
