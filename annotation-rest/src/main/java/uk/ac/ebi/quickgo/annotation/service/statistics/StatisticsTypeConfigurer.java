package uk.ac.ebi.quickgo.annotation.service.statistics;

import java.util.List;
import java.util.Map;

/**
 * // TODO: 16/08/17  
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
        Map<String, Integer> subFacetItems = properties;

        for (RequiredStatistic request : requests) {
            for (RequiredStatisticType requestType : request.getTypes()) {
                String requestTypeName = requestType.getName();
                if (subFacetItems.containsKey(requestTypeName)) {
                    requestType.setLimit(subFacetItems.get(requestTypeName));
                }
            }
        }
    }
}
