package uk.ac.ebi.quickgo.annotation.service.statistics;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;
import uk.ac.ebi.quickgo.rest.search.query.AggregateRequest;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * A simple implementation of the {@link StatsConverter} interface.
 *
 * @author Ricardo Antunes
 */
@Component
public class StatsConverterImpl implements StatsConverter {

    static final String DEFAULT_GLOBAL_AGGREGATE_NAME = "global";

    @Override public AggregateRequest convert(Collection<RequiredStatistic> requiredStatistics) {
        Preconditions.checkArgument(requiredStatistics != null && !requiredStatistics.isEmpty(),
                "Stats request collection cannot be null or empty");

        Map<String, AggregateRequest> nestedAggregateMap = new HashMap<>();
        AggregateRequest globalAggregate = new AggregateRequest(DEFAULT_GLOBAL_AGGREGATE_NAME);

        requiredStatistics.forEach(request -> {
            globalAggregate.addField(request.getGroupField(), AggregateFunction.typeOf(request.getAggregateFunction()));
            request.getTypes().forEach(type -> {
                String typeName = type.getName();
                if (!nestedAggregateMap.containsKey(typeName)) {
                    AggregateRequest aggregateRequestForType = new AggregateRequest(typeName, type.getLimit());
                    nestedAggregateMap.put(typeName, aggregateRequestForType);
                }

                AggregateRequest aggregateForType = nestedAggregateMap.get(typeName);
                aggregateForType.addField(request.getGroupField(),
                        AggregateFunction.typeOf(request.getAggregateFunction()));
            });
        });

        // add all values of map as nested aggregates to global aggregate
        nestedAggregateMap.values().forEach(globalAggregate::addNestedAggregate);

        return globalAggregate;
    }

}
