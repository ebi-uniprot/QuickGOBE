package uk.ac.ebi.quickgo.annotation.service.statistics;

import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A source of {@link RequiredStatistics} instances.
 *
 * @author Tony Wardell
 * Date: 19/12/2017
 * Time: 11:04
 * Created with IntelliJ IDEA.
 */
public class RequiredStatisticsProvider {

    private final RequiredStatistics standardUsage;
    private final RequiredStatistics downloadUsage;
    private final RequiredStatistics standardUsageWithGeneProductFiltering;
    private final RequiredStatistics downloadUsageWithGeneProductFiltering;

    public RequiredStatisticsProvider(StatisticsTypeConfigurer standardConfiguration, StatisticsTypeConfigurer
            downloadConfiguration) {
        checkArgument(Objects.nonNull(standardConfiguration), "The standard StatisticsTypeConfigurer instance cannot" +
                " be null");
        checkArgument(Objects.nonNull(standardConfiguration), "The download StatisticsTypeConfigurer instance cannot" +
                " be null");
        standardUsage = new RequiredStatistics(standardConfiguration);
        downloadUsage = new RequiredStatistics(downloadConfiguration);
        standardUsageWithGeneProductFiltering = new RequiredStatisticsWithGeneProduct(standardConfiguration);
        downloadUsageWithGeneProductFiltering = new RequiredStatisticsWithGeneProduct(downloadConfiguration);
    }

    public List<RequiredStatistic> getStandardUsage() {
        return standardUsage.getRequiredStatistics();
    }

    public List<RequiredStatistic> getDownloadUsage() {
        return downloadUsage.getRequiredStatistics();
    }

    public List<RequiredStatistic> getStandardUsageWithGeneProductFiltering() {
        return standardUsageWithGeneProductFiltering.getRequiredStatistics();
    }

    public List<RequiredStatistic> getDownloadUsageWithGeneProductFiltering() {
        return downloadUsageWithGeneProductFiltering.getRequiredStatistics();
    }
}
