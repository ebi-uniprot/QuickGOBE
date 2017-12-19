package uk.ac.ebi.quickgo.annotation.service.statistics;

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
    final RequiredStatistics withGeneProduct;

    public RequiredStatisticsProvider(StatisticsTypeConfigurer statsConfigurer) {
        usualCase = new RequiredStatistics(statsConfigurer);
        withGeneProduct = new RequiredStatisticsWithGeneProduct(statsConfigurer);
    }
}
