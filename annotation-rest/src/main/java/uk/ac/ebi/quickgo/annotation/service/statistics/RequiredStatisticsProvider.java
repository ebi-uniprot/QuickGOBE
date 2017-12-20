package uk.ac.ebi.quickgo.annotation.service.statistics;

import java.util.Map;


/**
 * A source of {@link RequiredStatistics} instances.
 *
 * @author Tony Wardell
 * Date: 19/12/2017
 * Time: 11:04
 * Created with IntelliJ IDEA.
 */
public class RequiredStatisticsProvider {

    final RequiredStatistics usualCase;
    final RequiredStatistics usualCaseForDownload;
    final RequiredStatistics withGeneProduct;
    final RequiredStatistics withGeneProductForDownload;

    public RequiredStatisticsProvider(Map<String, Integer> usualProperties, Map<String, Integer> downLoadProperties) {
        StatisticsTypeConfigurer usualConfigurer = new StatisticsTypeConfigurer(usualProperties);
        StatisticsTypeConfigurer downloadConfigurer = new StatisticsTypeConfigurer(downLoadProperties);
        usualCase = new RequiredStatistics(usualConfigurer);
        usualCaseForDownload = new RequiredStatistics(downloadConfigurer);
        withGeneProduct = new RequiredStatisticsWithGeneProduct(usualConfigurer);
        withGeneProductForDownload = new RequiredStatistics(downloadConfigurer);
    }
}
