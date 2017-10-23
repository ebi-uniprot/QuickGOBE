package uk.ac.ebi.quickgo.annotation.service.statistics;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides configuration information for the statistics service
 *
 * Created 14/08/17
 * @author Edd
 */
@Configuration
@ConfigurationProperties(prefix = "annotation.stats")
public class StatisticsServiceConfig {
    private Map<String, Integer> typeLimitsForStandardUsage = new HashMap<>();
    private Map<String, Integer> typeLimitsForDownload = new HashMap<>();

    public Map<String, Integer> getTypeLimitsForStandardUsage() {
        return typeLimitsForStandardUsage;
    }

    public void setTypeLimitsForStandardUsage(Map<String, Integer> typeLimitsForStandardUsage) {
        this.typeLimitsForStandardUsage = typeLimitsForStandardUsage;
    }

    public Map<String, Integer> getTypeLimitsForDownload() {
        return typeLimitsForDownload;
    }

    public void setTypeLimitsForDownload(Map<String, Integer> typeLimitsForDownload) {
        this.typeLimitsForDownload = typeLimitsForDownload;
    }

    @Bean
    public RequiredStatistics requiredStatisticsForStandardUsage(
            StatisticsTypeConfigurer statsTypeConfigurerForStandardUsage) {
        return new RequiredStatistics(statsTypeConfigurerForStandardUsage);
    }

    @Bean
    public RequiredStatistics requiredStatisticsForDownloadUsage(
            StatisticsTypeConfigurer statsTypeConfigurerForDownloadUsage) {
        return new RequiredStatistics(statsTypeConfigurerForDownloadUsage);
    }

    @Bean
    public StatisticsTypeConfigurer statsTypeConfigurerForStandardUsage() {
        return new StatisticsTypeConfigurer(typeLimitsForStandardUsage);
    }

    @Bean
    public StatisticsTypeConfigurer statsTypeConfigurerForDownloadUsage() {
        return new StatisticsTypeConfigurer(typeLimitsForDownload);
    }
}
