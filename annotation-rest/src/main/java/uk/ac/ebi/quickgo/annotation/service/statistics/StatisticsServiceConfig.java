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

    public Map<String, Integer> getTypeLimits() {
        return typeLimits;
    }

    public void setTypeLimits(Map<String, Integer> typeLimits) {
        this.typeLimits = typeLimits;
    }

    @Bean
    public RequiredStatistics requiredStatistics(StatisticsTypeConfigurer statsTypeConfigurer) {
        return new RequiredStatistics(statsTypeConfigurer);
    }

    @Bean
    public StatisticsTypeConfigurer statsTypeConfigurer() {
        return new StatisticsTypeConfigurer(typeLimits);
    }
}