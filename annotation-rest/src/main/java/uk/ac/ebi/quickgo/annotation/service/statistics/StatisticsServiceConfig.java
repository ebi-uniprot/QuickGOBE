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
    private Map<String, Integer> typeLimitsForDownloadUsage = new HashMap<>();

    public Map<String, Integer> getTypeLimitsForStandardUsage() {
        return typeLimitsForStandardUsage;
    }

    public void setTypeLimitsForStandardUsage(Map<String, Integer> typeLimitsForStandardUsage) {
        this.typeLimitsForStandardUsage = typeLimitsForStandardUsage;
    }

    public Map<String, Integer> getTypeLimitsForDownloadUsage() {
        return typeLimitsForDownloadUsage;
    }

    public void setTypeLimitsForDownloadUsage(Map<String, Integer> typeLimitsForDownloadUsage) {
        this.typeLimitsForDownloadUsage = typeLimitsForDownloadUsage;
    }

    @Bean
    public RequiredStatisticsProvider statisticsProvider() {
        return new RequiredStatisticsProvider(standardConfiguration(), downloadConfiguration());
    }

    private StatisticsTypeConfigurer standardConfiguration() {
        return new StatisticsTypeConfigurer(typeLimitsForStandardUsage);
    }

    private StatisticsTypeConfigurer downloadConfiguration() {
        return new StatisticsTypeConfigurer(typeLimitsForDownloadUsage);
    }
}
