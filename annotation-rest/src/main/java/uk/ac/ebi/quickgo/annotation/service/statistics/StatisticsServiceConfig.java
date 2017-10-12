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
    private Map<String, Integer> typeLimits = new HashMap<>();
    private Map<String, Integer> typeLimitsForDownload = new HashMap<>();

    public Map<String, Integer> getTypeLimits() {
        return typeLimits;
    }

    public Map<String, Integer> getTypeLimitsForDownload() {
        return typeLimitsForDownload;
    }

    public void setTypeLimits(Map<String, Integer> typeLimits) {
        this.typeLimits = typeLimits;
    }

    public void setTypeLimitsForDownload(Map<String, Integer> typeLimitsForDownload) {
        this.typeLimitsForDownload = typeLimitsForDownload;
    }

    @Bean
    public RequiredStatistics listStatistics(StatisticsTypeConfigurer statsTypeConfigurerForList) {
        return new RequiredStatistics(statsTypeConfigurerForList);
    }

    @Bean
    public RequiredStatistics downloadStatistics(StatisticsTypeConfigurer statsTypeConfigurerForDownload) {
        return new RequiredStatistics(statsTypeConfigurerForDownload);
    }

    @Bean
    public StatisticsTypeConfigurer statsTypeConfigurerForList() {
        return new StatisticsTypeConfigurer(typeLimits);
    }

    @Bean
    public StatisticsTypeConfigurer statsTypeConfigurerForDownload() {
        return new StatisticsTypeConfigurer(typeLimitsForDownload);
    }
}
