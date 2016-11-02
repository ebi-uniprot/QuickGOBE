package uk.ac.ebi.quickgo.annotation.service.statistics;

import uk.ac.ebi.quickgo.annotation.model.AnnotationRequest;
import uk.ac.ebi.quickgo.rest.search.AggregateFunction;
import uk.ac.ebi.quickgo.rest.search.query.AggregateRequest;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * A simple implementation of the {@link StatsRequestConverter} interface.
 *
 * @author Ricardo Antunes
 */
@Component
public class StatsRequestConverterImpl implements StatsRequestConverter {

    static final String DEFAULT_GLOBAL_AGGREGATE_NAME = "global";

    @Override public AggregateRequest convert(Collection<AnnotationRequest.StatsRequest> statsRequests) {
        Preconditions.checkArgument(statsRequests != null && statsRequests.size() > 0,
                "Stats request collection cannot be null or empty");

        Map<String, AggregateRequest> nestedAggregateMap = new HashMap<>();
        AggregateRequest globalAggregate = new AggregateRequest(DEFAULT_GLOBAL_AGGREGATE_NAME);

        statsRequests.forEach(request -> {
            globalAggregate.addField(request.getGroupField(), AggregateFunction.typeOf(request.getAggregateFunction()));
            request.getTypes().forEach(type -> {
                if (!nestedAggregateMap.containsKey(type)) {
                    nestedAggregateMap.put(type, new AggregateRequest(type));
                }

                AggregateRequest aggregateForType = nestedAggregateMap.get(type);
                aggregateForType.addField(request.getGroupField(),
                        AggregateFunction.typeOf(request.getAggregateFunction()));
            });
        });

        // add all values of map as nested aggregates to global aggregate
        nestedAggregateMap.values().forEach(globalAggregate::addNestedAggregate);

        return globalAggregate;
    }

}
