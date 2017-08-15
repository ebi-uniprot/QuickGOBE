package uk.ac.ebi.quickgo.annotation.service.statistics;

import uk.ac.ebi.quickgo.annotation.common.AnnotationFields;

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
    static final int DEFAULT_GO_TERM_LIMIT = 200;
    private Map<String, Integer> typeLimits = defaultStatisticsTypeLimits();

    public Map<String, Integer> getTypeLimits() {
        return typeLimits;
    }

    public void setTypeLimits(Map<String, Integer> typeLimits) {
        this.typeLimits = typeLimits;
    }

    @Bean
    public StatisticsTypeConfigurer statsTypeConfigurer() {
        return new StatisticsTypeConfigurer(typeLimits);
    }

    private static Map<String, Integer> defaultStatisticsTypeLimits() {
        HashMap<String, Integer> limits = new HashMap<>();
        limits.put(AnnotationFields.Facetable.GO_ID, DEFAULT_GO_TERM_LIMIT);
        return limits;
    }
}