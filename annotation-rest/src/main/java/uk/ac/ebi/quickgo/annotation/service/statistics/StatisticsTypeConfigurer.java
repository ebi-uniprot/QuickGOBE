package uk.ac.ebi.quickgo.annotation.service.statistics;

import java.util.List;
import java.util.Map;

/**
 * This class configures the {@link RequiredStatisticType} values within a list of {@link RequiredStatistic}s.
 * 
 * Created 14/08/17
 * @author Edd
 */
public class StatisticsTypeConfigurer {
    private final Map<String, Integer> properties;

    public StatisticsTypeConfigurer(Map<String, Integer> properties) {
        this.properties = properties;
    }
    
    void configureStatsRequests(List<RequiredStatistic> requests) {
        for (RequiredStatistic request : requests) {
            for (RequiredStatisticType requestType : request.getTypes()) {
                String requestTypeName = requestType.getName();
                if (properties.containsKey(requestTypeName)) {
                    requestType.setLimit(properties.get(requestTypeName));
                }
            }
        }
    }
}
