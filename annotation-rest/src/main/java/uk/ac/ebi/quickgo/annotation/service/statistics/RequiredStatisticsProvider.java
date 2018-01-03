package uk.ac.ebi.quickgo.annotation.service.statistics;

import java.util.List;

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
