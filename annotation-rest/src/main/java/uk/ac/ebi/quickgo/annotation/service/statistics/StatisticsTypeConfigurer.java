package uk.ac.ebi.quickgo.annotation.service.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static uk.ac.ebi.quickgo.annotation.service.statistics.RequiredStatisticType.statsType;

/**
 * This class uses a configuration map of types/limits to provide a configured representation of a list of
 * {@link RequiredStatisticType}s.
 * 
 * Created 14/08/17
 * @author Edd
 */
public class StatisticsTypeConfigurer {
    private final Map<String, Integer> properties;

    public StatisticsTypeConfigurer(Map<String, Integer> properties) {
        this.properties = properties;
    }
    
    List<RequiredStatisticType> getConfiguredStatsTypes(List<RequiredStatisticType> types) {
        List<RequiredStatisticType> configuredTypes = new ArrayList<>();

        for (RequiredStatisticType type : types) {
            String typeName = type.getName();

            if (properties.containsKey(typeName)) {
                configuredTypes.add(statsType(typeName, properties.get(typeName)));
            } else {
                configuredTypes.add(statsType(typeName, type.getLimit()));
            }
        }

        return configuredTypes;
    }

}
