package uk.ac.ebi.quickgo.annotation.service.statistics;

import uk.ac.ebi.quickgo.annotation.model.AnnotationRequest;

import java.util.List;
import java.util.Map;

/**
 * Created 14/08/17
 * @author Edd
 */
public class StatisticsTypeConfigurer {
    private final Map<String, Integer> properties;

    public StatisticsTypeConfigurer(Map<String, Integer> properties) {
        this.properties = properties;
    }
    
    void configureStatsRequests(List<AnnotationRequest.StatsRequest> requests) {
        Map<String, Integer> subFacetItems = properties;

        for (AnnotationRequest.StatsRequest request : requests) {
            for (AnnotationRequest.StatsRequestType requestType : request.getTypes()) {
                String requestTypeName = requestType.getName();
                if (subFacetItems.containsKey(requestTypeName)) {
                    requestType.setLimit(subFacetItems.get(requestTypeName));
                }
            }
        }
    }
}
